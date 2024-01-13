package pl.mimuw.worker.service;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.cloud.spring.pubsub.core.publisher.PubSubPublisherTemplate;
import com.google.cloud.spring.pubsub.core.subscriber.PubSubSubscriberTemplate;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.SneakyThrows;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.mimuw.evt.schemas.MonitorTaskMessage;
import pl.mimuw.worker.AbstractIT;
import pl.mimuw.worker.entity.MonitorResult;
import pl.mimuw.worker.entity.MonitorResultEntity;
import pl.mimuw.worker.repository.MonitorResultRepository;

import java.util.Date;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasSize;
import static pl.mimuw.worker.utils.TimeUtils.currentTimeSecsPlus;

public class WorkerServiceIT extends AbstractIT {

    @Autowired
    private WorkerConfiguration workerConfiguration;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private PubSubPublisherTemplate publisherTemplate;

    @Autowired
    private PubSubSubscriberTemplate subscriberTemplate;

    @Autowired
    private MonitorResultRepository monitorResultRepository;

    private static final String PROJECT_ID = "test-project-123";
    private static final String SUBSCRIPTION_ID = "test-subscription";
    private static final String TOPIC_ID = "test-topic";

    @BeforeAll
    @SneakyThrows
    static void setup() {
        ManagedChannel channel = ManagedChannelBuilder.forTarget("dns:///" + pubSubEmulator.getEmulatorEndpoint())
                .usePlaintext()
                .build();
        TransportChannelProvider channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));

        TopicAdminClient topicAdminClient = TopicAdminClient.create(TopicAdminSettings.newBuilder()
                .setCredentialsProvider(NoCredentialsProvider.create())
                .setTransportChannelProvider(channelProvider)
                .build());

        SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create(SubscriptionAdminSettings.newBuilder()
                .setTransportChannelProvider(channelProvider)
                .setCredentialsProvider(NoCredentialsProvider.create())
                .build());

        PubSubAdmin admin = new PubSubAdmin(() -> PROJECT_ID, topicAdminClient, subscriptionAdminClient);

        admin.createTopic(TOPIC_ID);
        admin.createSubscription(SUBSCRIPTION_ID, TOPIC_ID, 10);

        admin.close();
        channel.shutdown();
    }

    @AfterEach
    void teardown() {
        monitorResultRepository.deleteAll();
        workerService.gracefullyShutdown();
        await().until(() -> subscriberTemplate.pullAndAck(SUBSCRIPTION_ID, 1000, true), hasSize(0));
    }

    @Test
    @SneakyThrows
    void workerServicePullsTasksFromQueueTest() {
        // given
        final var message = createMonitorTaskMessageBuilder().build();
        publisherTemplate.publish(TOPIC_ID, message).get();

        // when
        final var result = workerService.pullMessages();

        // then
        Assertions.assertEquals(1, result.size());
        final var resultMessage = result.get(0);
        final var messagePayload = resultMessage.getPayload();

        Assertions.assertEquals(message.getJobId(), messagePayload.getJobId());
        Assertions.assertEquals(message.getServiceUrl(), messagePayload.getServiceUrl());
        Assertions.assertEquals(message.getPollFrequencySecs(), messagePayload.getPollFrequencySecs());
        Assertions.assertEquals(message.getTaskDeadlineTimestampSecs(), messagePayload.getTaskDeadlineTimestampSecs());
        resultMessage.ack().get();
    }

    @Test
    @SneakyThrows
    void workerServiceProcessesTaskMessagesTest() {
        // given
        final var pollTimes = 3;
        final var pollFrequencySecs = 2;
        final var neededTimeSecs = (pollTimes - 1) * pollFrequencySecs + 1;
        final var message = createMonitorTaskMessageBuilder()
                .setPollFrequencySecs(pollFrequencySecs)
                .setTaskDeadlineTimestampSecs(currentTimeSecsPlus(neededTimeSecs))
                .build();
        publisherTemplate.publish(TOPIC_ID, message).get();

        // when
        workerService.pullAndProcessMonitorTaskMessages();
        Thread.sleep(neededTimeSecs * 1000);

        // then
        final var results = monitorResultRepository.findAll();
        Assertions.assertEquals(pollTimes, results.size());
    }

    @Test
    @SneakyThrows
    void workerServiceProcessesMaxNumberOfTasks() {
        // given
        final var moreThanMaxTasks = workerConfiguration.getMaxTasksPerPod() + 5;
        for (int i = 0; i < moreThanMaxTasks; i++) {
            final var message = createMonitorTaskMessageBuilder()
                    .setPollFrequencySecs(100)
                    .setTaskDeadlineTimestampSecs(currentTimeSecsPlus(3))
                    .build();
            publisherTemplate.publish(TOPIC_ID, message).get();
        }

        // when
        workerService.pullAndProcessMonitorTaskMessages();
        Thread.sleep(2000);

        // then
        final var results = monitorResultRepository.findAll();
        Assertions.assertEquals(workerConfiguration.getMaxTasksPerPod(), results.size());
        final var jobIds = results.stream().map(MonitorResultEntity::getJobId).distinct();
        Assertions.assertEquals(workerConfiguration.getMaxTasksPerPod(), (int) jobIds.count());
    }

    @Test
    @SneakyThrows
    void workerServiceExtendsAckDeadlinesForMessagesTest() {
        // given
        final var extendTimes = 3;
        final var message = createMonitorTaskMessageBuilder()
                .setTaskDeadlineTimestampSecs(currentTimeSecsPlus(extendTimes * workerConfiguration.getAckDeadlineSecs()))
                .build();
        publisherTemplate.publish(TOPIC_ID, message).get();

        // when
        workerService.pullAndProcessMonitorTaskMessages();

        // then
        for (int j = 0; j < extendTimes; j++) {
            workerService.extendAckDeadlinesForMonitorTaskMessages();
            for (int i = 0; i < (workerConfiguration.getAckDeadlineSecs() - 1); i++) {
                Thread.sleep(1000);
                Assertions.assertTrue(isQueueEmpty());
            }
        }

        // wait for the message to get back to the queue
        Thread.sleep(1500);
        // DISCLAIMER: this is not acceptable behavior for production code, because
        // we would duplicate the jobs across the workers - this is only for testing purposes
        Assertions.assertFalse(isQueueEmpty());
    }

    @Test
    @SneakyThrows
    void workerServiceDontProcessExpiredMessagesTest() {
        // given
        final var message = createMonitorTaskMessageBuilder()
                .setTaskDeadlineTimestampSecs(currentTimeSecsPlus(-1))
                .build();
        publisherTemplate.publish(TOPIC_ID, message).get();

        // when
        workerService.pullAndProcessMonitorTaskMessages();
        Thread.sleep(1000);

        // then
        Assertions.assertTrue(monitorResultRepository.findAll().isEmpty());
    }

    @Test
    @SneakyThrows
    void workerServiceStartsMessageProcessingWithProperDelayTest() {
        // given
        final var jobId = UUID.randomUUID();

        final var olderMockedResult = new MonitorResultEntity();
        olderMockedResult.setId(ObjectId.get());
        olderMockedResult.setJobId(jobId);
        olderMockedResult.setResult(MonitorResult.SUCCESS);
        olderMockedResult.setTimestamp(new Date());
        monitorResultRepository.save(olderMockedResult);

        final var mockedResult = new MonitorResultEntity();
        mockedResult.setId(ObjectId.get());
        mockedResult.setJobId(jobId);
        mockedResult.setResult(MonitorResult.SUCCESS);
        mockedResult.setTimestamp(new Date());
        monitorResultRepository.save(mockedResult);

        final var pollFrequencySecs = 3;
        final var message = createMonitorTaskMessageBuilder()
                .setJobId(jobId.toString())
                .setPollFrequencySecs(pollFrequencySecs)
                .setTaskDeadlineTimestampSecs(currentTimeSecsPlus(pollFrequencySecs + 1))
                .build();
        publisherTemplate.publish(TOPIC_ID, message).get();

        // when
        workerService.pullAndProcessMonitorTaskMessages();
        Thread.sleep((pollFrequencySecs + 1) * 1000);

        // then
        final var resultOpt = monitorResultRepository.findTopByJobIdOrderByTimestampDesc(jobId);
        Assertions.assertTrue(resultOpt.isPresent());

        final var result = resultOpt.get();
        Assertions.assertNotEquals(olderMockedResult.getId(), result.getId());
        Assertions.assertNotEquals(mockedResult.getId(), result.getId());

        final var timestampMillisDiff = result.getTimestamp().getTime() - mockedResult.getTimestamp().getTime();
        Assertions.assertTrue(pollFrequencySecs * 1000 <= (timestampMillisDiff + 1000));
        Assertions.assertTrue(pollFrequencySecs * 1000 >= (timestampMillisDiff - 1000));
    }

    private MonitorTaskMessage.Builder createMonitorTaskMessageBuilder() {
        return MonitorTaskMessage.newBuilder()
                .setJobId(UUID.randomUUID().toString())
                .setServiceUrl("http://test-service-url")
                .setPollFrequencySecs(1)
                .setTaskDeadlineTimestampSecs(currentTimeSecsPlus(1));
    }

    private boolean isQueueEmpty() {
        return subscriberTemplate.pullAndAck(SUBSCRIPTION_ID, 1000, true).isEmpty();
    }
}
