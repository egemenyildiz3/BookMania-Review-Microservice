package nl.tudelft.sem.template.review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Example microservice application.
 */
@SpringBootApplication
@EnableJpaRepositories
@EntityScan("nl.tudelft.sem.template.model") // Specify the package containing your entities
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
