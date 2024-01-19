package pl.mimuw.worker.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "monitor")
public class MonitorConfiguration {

    private String projectId;
    private String metricsName;
    private Integer pingTimeoutSecs;

    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }
}
