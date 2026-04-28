package pl.edu.agh.to.backendspringboot;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BackendSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendSpringBootApplication.class, args);
    }

}
