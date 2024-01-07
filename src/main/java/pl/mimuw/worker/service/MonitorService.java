package pl.mimuw.worker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.mimuw.worker.entity.MonitorResultEntity;
import pl.mimuw.worker.repository.MonitorResultRepository;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MonitorService {

    private final WebClient webClient;
    private final MonitorResultRepository monitorResultRepository;

    public void pingHostAndSaveResult(final String jobId, final String url) {
        final var result = webClient.get()
                .uri(url)
                .exchangeToMono(clientResponse -> Mono.just(clientResponse.statusCode()))
                .block();

        if (result == null) {
            throw new RuntimeException("No response from host");
        }

        final var monitorResultEntity = new MonitorResultEntity();
        monitorResultEntity.setId(UUID.randomUUID());
        monitorResultEntity.setJobId(UUID.fromString(jobId));
        monitorResultEntity.setStatusCode(result.value());
        monitorResultEntity.setTimestamp(new Date());
        monitorResultRepository.save(monitorResultEntity);
    }
}
