package pl.mimuw.worker.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
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

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MonitorService {

    private final WebClient webClient;
    private final MonitorResultRepository monitorResultRepository;
    private final MonitorConfiguration monitorConfiguration;

    public void pingHostAndSaveResult(final String jobId, final String url) {
        final var result = pingHost(url);
        final var monitorResultEntity = new MonitorResultEntity();
        monitorResultEntity.setId(ObjectId.get());
        monitorResultEntity.setJobId(UUID.fromString(jobId));
        monitorResultEntity.setResult(result);
        monitorResultEntity.setTimestamp(TimeUtils.currentTimeSecs());
        monitorResultRepository.save(monitorResultEntity);
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
