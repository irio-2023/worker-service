package pl.mimuw.template.web;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.mimuw.template.AbstractIT;

import static io.restassured.RestAssured.given;

public class HealthCheckControllerIT extends AbstractIT {

    @Test
    void health() {
        // when
        final String status = given()
                .when()
                .get(getBaseUrl() + "/health")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        // then
        Assertions.assertEquals("OK", status);
    }
}
