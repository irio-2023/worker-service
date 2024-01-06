package pl.mimuw.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WorkerServiceApplication {

    // Swagger UI: http://localhost:8080/swagger-ui.html
    public static void main(String[] args) {
        SpringApplication.run(WorkerServiceApplication.class, args);
    }

}
