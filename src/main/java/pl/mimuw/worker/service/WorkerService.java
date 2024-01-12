package pl.mimuw.worker.service;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.converter.ConvertedAcknowledgeablePubsubMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.mimuw.evt.schemas.MonitorTaskMessage;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static pl.mimuw.worker.utils.TimeUtils.currentDate;
import static pl.mimuw.worker.utils.TimeUtils.currentTimeSecs;
import static pl.mimuw.worker.utils.TimeUtils.currentTimeSecsPlus;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WorkerService {

    private static final Long NO_INITIAL_DELAY = 0L;

    private final WorkerConfiguration workerConfiguration;
    private final ScheduledExecutorService scheduledExecutorService;
    private final PubSubTemplate pubSubTemplate;
    private final MonitorService monitorService;

    private final Map<String, AcknowledgeablePubsubMessage> messageAcks = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> messageFutures = new ConcurrentHashMap<>();

    @Scheduled(cron = "${worker.pullCron}")
    public void pullAndProcessMonitorTaskMessages() {
        log.info("Pulling messages from task queue, time: {}", currentDate());
        final var messages = pullMessages();
        log.info("Pulled {} messages from task queue", messages.size());

        messages.forEach(message -> {
            log.info("Starting processing message: {}", message.getAckId());
            message.modifyAckDeadline(workerConfiguration.getAckDeadlineSecs());
            final var payload = message.getPayload();
            final var future = scheduledExecutorService.scheduleAtFixedRate(
                    () -> processMessage(message.getAckId(), payload),
                    getInitialDelayForJob(payload.getJobId().toString(), payload.getPollFrequencySecs()),
                    payload.getPollFrequencySecs(),
                    TimeUnit.SECONDS);

            messageAcks.put(message.getAckId(), message);
            messageFutures.put(message.getAckId(), future);
        });
    }

    @Scheduled(cron = "${worker.extendAckCron}")
    public void extendAckDeadlinesForMonitorTaskMessages() {
        log.info("Extending ack deadlines for messages, time: {}", currentDate());
        messageAcks.values().forEach(message ->
                message.modifyAckDeadline(workerConfiguration.getAckDeadlineSecs())
        );
    }

    public List<ConvertedAcknowledgeablePubsubMessage<MonitorTaskMessage>> pullMessages() {
        return pubSubTemplate.pullAndConvert(
                workerConfiguration.getSubscriptionId(),
                workerConfiguration.getMaxTasksPerPod() - messageAcks.size(),
                true, MonitorTaskMessage.class
        );
    }

    private void processMessage(final String ackId, final MonitorTaskMessage monitorTask) {
        log.info("Pinging service: {}, time: {}", monitorTask.getServiceUrl(), currentDate());
        monitorService.pingHostAndSaveResult(monitorTask.getJobId().toString(), monitorTask.getServiceUrl().toString());

        if (currentTimeSecsPlus(monitorTask.getPollFrequencySecs()) > monitorTask.getTaskDeadlineTimestampSecs()) {
            log.info("Task deadline exceeded, finishing processing message: {}", ackId);
            messageAcks.remove(ackId).ack();
            messageFutures.remove(ackId).cancel(false);
        }
    }

    private long getInitialDelayForJob(final String jobId, final long pollFrequencySecs) {
        return monitorService.getLatestMonitorResultByJobId(UUID.fromString(jobId))
                .map(result -> calculateDelay(result.getTimestamp().toInstant().getEpochSecond(), pollFrequencySecs))
                .orElse(NO_INITIAL_DELAY);
    }

    private long calculateDelay(final long lastPingTimestamp, final long pollFrequencySecs) {
        final var delay = lastPingTimestamp + pollFrequencySecs - currentTimeSecs();
        return delay > 0 ? delay : NO_INITIAL_DELAY;
    }
}
