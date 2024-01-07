package pl.mimuw.worker;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PubSubEmulatorContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@DirtiesContext
@AutoConfigureObservability
@Testcontainers
@ActiveProfiles("it-test")
@SpringBootTest(classes = WorkerServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIT {

    @TestConfiguration
    static class PubSubEmulatorConfiguration {
        @Bean
        CredentialsProvider googleCredentials() {
            return NoCredentialsProvider.create();
        }
    }

    @Container
    static final PubSubEmulatorContainer pubSubEmulator = new PubSubEmulatorContainer(
            DockerImageName.parse("gcr.io/google.com/cloudsdktool/google-cloud-cli:441.0.0-emulators")
    );

    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer(
            DockerImageName.parse("mongo:5.0.2")
    );

    @DynamicPropertySource
    static void emulatorProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.gcp.pubsub.emulator-host", pubSubEmulator::getEmulatorEndpoint);
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @LocalServerPort
    protected Integer port;

    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }
}
