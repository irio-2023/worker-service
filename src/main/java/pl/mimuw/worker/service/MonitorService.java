package pl.mimuw.worker.service;

import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.monitoring.v3.CreateTimeSeriesRequest;
import com.google.monitoring.v3.ProjectName;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import pl.mimuw.worker.entity.MonitorResult;
import pl.mimuw.worker.entity.MonitorResultEntity;
import pl.mimuw.worker.repository.MonitorResultRepository;
import pl.mimuw.worker.utils.TimeUtils;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static pl.mimuw.worker.utils.MetricsUtils.createListOfTimeSeries;
import static pl.mimuw.worker.utils.TimeUtils.currentDate;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MonitorService {

    private final WebClient webClient;
    private final MonitorResultRepository monitorResultRepository;
    private final MonitorConfiguration monitorConfiguration;

    private final AtomicInteger numberOfPings = new AtomicInteger(0);

    @SneakyThrows
    @Scheduled(cron = "${monitor.metricsCron}")
    public void sendNumberOfPingsMetric() {
        log.info("Sending number of pings, time: {}", currentDate());
        final var projectName = ProjectName.of(monitorConfiguration.getProjectId());

        final var request = CreateTimeSeriesRequest.newBuilder()
                .setName(projectName.toString())
                .addAllTimeSeries(createListOfTimeSeries(
                        monitorConfiguration.getProjectId(),
                        monitorConfiguration.getMetricsName(),
                        numberOfPings.getAndSet(0)))
                .build();

        try (final MetricServiceClient client = MetricServiceClient.create()) {
            client.createTimeSeries(request);
            log.info("Successfully sent number of pings");
        }
    }

    public void pingHostAndSaveResult(final String jobId, final String url) {
        final var result = pingHost(url);
        final var monitorResultEntity = new MonitorResultEntity();
        monitorResultEntity.setId(ObjectId.get());
        monitorResultEntity.setJobId(UUID.fromString(jobId));
        monitorResultEntity.setResult(result);
        monitorResultEntity.setTimestamp(TimeUtils.currentTimeSecs());
        monitorResultEntity.setExpiresAt(TimeUtils.getExpiresAt());
        monitorResultRepository.save(monitorResultEntity);
        numberOfPings.incrementAndGet();
    }

    public Optional<MonitorResultEntity> getLatestMonitorResultByJobId(final UUID jobId) {
        return monitorResultRepository.findTopByJobIdOrderByTimestampDesc(jobId);
    }

    private MonitorResult pingHost(final String url) {
        final MonitorResult result = webClient
                .get()
                .uri(url)
                .exchangeToMono(clientResponse -> Mono.just(MonitorResult.fromStatusCode(clientResponse.statusCode())))
                .timeout(Duration.ofSeconds(monitorConfiguration.getPingTimeoutSecs()))
                .onErrorReturn(TimeoutException.class, MonitorResult.ERROR_TIMEOUT)
                .onErrorReturn(WebClientException.class, MonitorResult.ERROR_DNS)
                .onErrorReturn(Exception.class, MonitorResult.ERROR_NO_RESPONSE)
                .block();
        return Objects.requireNonNullElse(result, MonitorResult.ERROR_NO_RESPONSE);
    }
}
