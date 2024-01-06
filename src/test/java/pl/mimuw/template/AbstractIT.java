package pl.mimuw.template;

import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@DirtiesContext
@AutoConfigureObservability
@ActiveProfiles("it-test")
@SpringBootTest(classes = TemplateServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIT {

    @LocalServerPort
    protected Integer port;

    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }
}
