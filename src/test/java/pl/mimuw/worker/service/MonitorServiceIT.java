package pl.mimuw.worker.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import pl.mimuw.worker.AbstractIT;
import pl.mimuw.worker.entity.MonitorResult;
import pl.mimuw.worker.repository.MonitorResultRepository;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class MonitorServiceIT extends AbstractIT {

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private MonitorResultRepository monitorResultRepository;

    private static WireMockServer wireMockServer;
    private static String mockUrl;

    @BeforeAll
    public static void setup() {
        startWireMockServer();
    }

    @AfterAll
    public static void teardown() {
        stopWireMockServer();
    }

    private static void startWireMockServer() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        mockUrl = "http://localhost:" + wireMockServer.port();
    }

    private static void stopWireMockServer() {
        wireMockServer.stop();
    }

    @ParameterizedTest(name = "Test monitor service with {0} status code")
    @ValueSource(ints = {200, 301, 404, 500})
    void testMonitorService(int statusCode) {
        // given
        stubFor(get(urlEqualTo("/monitor"))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"OK\"}")));

        // when
        final UUID jobId = UUID.randomUUID();
        monitorService.pingHostAndSaveResult(jobId.toString(), mockUrl + "/monitor");

        // then
        monitorResultRepository.findByJobId(jobId).ifPresentOrElse(monitorResultEntity -> Assertions.assertEquals(
                monitorService.resultFromStatusCode(HttpStatusCode.valueOf(statusCode)),
                monitorResultEntity.getResult()), Assertions::fail);
    }

    @Test
    void testMonitorServiceWithAcceptableDelay() {
        // given
        stubFor(get(urlEqualTo("/monitor"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"OK\"}")
                        .withFixedDelay(3500)));

        // when
        final UUID jobId = UUID.randomUUID();
        monitorService.pingHostAndSaveResult(jobId.toString(), mockUrl + "/monitor");

        // then
        monitorResultRepository.findByJobId(jobId).ifPresentOrElse(monitorResultEntity -> Assertions.assertEquals(
                monitorService.resultFromStatusCode(HttpStatusCode.valueOf(200)),
                monitorResultEntity.getResult()), Assertions::fail);
    }

    @Test
    void testMonitorServiceWithUnacceptableDelay() {
        // given
        stubFor(get(urlEqualTo("/monitor"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"OK\"}")
                        .withFixedDelay(5500)));

        // when
        final UUID jobId = UUID.randomUUID();
        monitorService.pingHostAndSaveResult(jobId.toString(), mockUrl + "/monitor");

        // then
        monitorResultRepository.findByJobId(jobId).ifPresentOrElse(monitorResultEntity -> Assertions.assertEquals(
                MonitorResult.ERROR_TIMEOUT, monitorResultEntity.getResult()), Assertions::fail);
    }

    @Test
    void testMonitorServiceToNonExistingHost() {
        // given
        final var nonExistingHost = "http://non-existing-host:8080";

        // when
        final UUID jobId = UUID.randomUUID();
        monitorService.pingHostAndSaveResult(jobId.toString(), nonExistingHost);

        // then
        monitorResultRepository.findByJobId(jobId).ifPresentOrElse(monitorResultEntity -> Assertions.assertEquals(
                MonitorResult.ERROR_DNS, monitorResultEntity.getResult()), Assertions::fail);
    }
}
