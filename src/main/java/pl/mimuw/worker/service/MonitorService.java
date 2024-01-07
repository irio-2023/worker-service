package pl.mimuw.worker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import pl.mimuw.worker.entity.MonitorResult;
import pl.mimuw.worker.entity.MonitorResultEntity;
import pl.mimuw.worker.repository.MonitorResultRepository;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MonitorService {

    private final WebClient webClient;
    private final MonitorResultRepository monitorResultRepository;

    public void pingHostAndSaveResult(final String jobId, final String url) {
        final var result = pingHost(url);
        final var monitorResultEntity = new MonitorResultEntity();
        monitorResultEntity.setId(UUID.randomUUID());
        monitorResultEntity.setJobId(UUID.fromString(jobId));
        monitorResultEntity.setResult(result);
        monitorResultEntity.setTimestamp(new Date());
        monitorResultRepository.save(monitorResultEntity);
    }

    public MonitorResult pingHost(final String url) {
        MonitorResult result = webClient
                .get()
                .uri(url)
                .exchangeToMono(clientResponse -> Mono.just(resultFromStatusCode(clientResponse.statusCode())))
                .timeout(Duration.ofSeconds(5))
                .onErrorReturn(TimeoutException.class, MonitorResult.ERROR_TIMEOUT)
                .onErrorReturn(WebClientException.class, MonitorResult.ERROR_DNS)
                .onErrorReturn(Exception.class, MonitorResult.ERROR_NO_RESPONSE)
                .block();
        return Objects.requireNonNullElse(result, MonitorResult.ERROR_NO_RESPONSE);
    }

    public MonitorResult resultFromStatusCode(final HttpStatusCode statusCode) {
        if (statusCode.is2xxSuccessful() || statusCode.is3xxRedirection()) {
            return MonitorResult.SUCCESS;
        } else if (statusCode.is4xxClientError() || statusCode.is5xxServerError()) {
            return MonitorResult.FAILURE;
        } else {
            return MonitorResult.ERROR_NO_RESPONSE;
        }
    }
}
