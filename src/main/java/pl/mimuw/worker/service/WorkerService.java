package pl.mimuw.worker.service;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.converter.ConvertedAcknowledgeablePubsubMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.mimuw.evt.schemas.MonitorTaskMessage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static pl.mimuw.worker.utils.TimeUtils.currentDate;
import static pl.mimuw.worker.utils.TimeUtils.currentTimeSecs;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WorkerService {

    private final WorkerConfiguration workerConfiguration;
    private final ExecutorService executorService;
    private final PubSubTemplate pubSubTemplate;
    private final MonitorService monitorService;

    private final AtomicInteger currentlyProcessing = new AtomicInteger(0);
    private final Map<String, AcknowledgeablePubsubMessage> currentlyProcessingMessages = new ConcurrentHashMap<>();

    @Scheduled(cron = "${worker.pullCron}")
    public void pullAndProcessMonitorTaskMessages() {
        log.info("Pulling messages from task queue, time: {}", currentDate());
        final var messages = pullMessages();
        log.info("Pulled {} messages from task queue", messages.size());
        messages.forEach(message -> executorService.submit(() -> processMessage(message)));
    }

    @Scheduled(cron = "${worker.extendAckCron}")
    public void extendAckDeadlinesForMonitorTaskMessages() {
        log.info("Extending ack deadlines for messages, time: {}", currentDate());
        currentlyProcessingMessages.values().forEach(message -> {
            try {
                message.modifyAckDeadline(workerConfiguration.getAckDeadlineSecs());
            } catch (Exception e) {
                log.error("Error extending ack deadline for message: {}", message.getAckId(), e);
            }
        });
    }

    public List<ConvertedAcknowledgeablePubsubMessage<MonitorTaskMessage>> pullMessages() {
        return pubSubTemplate.pullAndConvert(
                workerConfiguration.getSubscriptionId(),
                workerConfiguration.getMaxTasksPerPod() - currentlyProcessing.get(),
                true, MonitorTaskMessage.class
        );
    }

    private void processMessage(final ConvertedAcknowledgeablePubsubMessage<MonitorTaskMessage> message) {
        try {
            log.info("Starting processing message: {}", message.getAckId());
            message.modifyAckDeadline(workerConfiguration.getAckDeadlineSecs());
            currentlyProcessing.incrementAndGet();
            currentlyProcessingMessages.put(message.getAckId(), message);
            monitorService(message.getPayload());
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
        } finally {
            currentlyProcessingMessages.remove(message.getAckId());
            currentlyProcessing.decrementAndGet();
            message.ack();
            log.info("Finished processing message: {}", message.getAckId());
        }
    }

    @SneakyThrows
    private void monitorService(final MonitorTaskMessage monitorTask) {
        for (int currentTimeSecs = currentTimeSecs();
             currentTimeSecs < monitorTask.getTaskDeadlineTimestampSecs();
             currentTimeSecs = currentTimeSecs()) {
            log.info("Pinging service: {}, time: {}", monitorTask.getServiceUrl(), currentDate());
            monitorService.pingHostAndSaveResult(monitorTask.getJobId().toString(), monitorTask.getServiceUrl().toString());
            Thread.sleep(monitorTask.getPollFrequencySecs() * 1000L);
        }
    }
}
