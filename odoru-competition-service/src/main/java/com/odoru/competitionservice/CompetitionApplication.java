package com.odoru.competitionservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Entry point for the Spring Boot Competition Application service. */
@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Odoru Competition Service API",
        version = "1.0",
        description = "Competition Management & Score Processing API"
    )
)
public class CompetitionApplication {

  public static void main(final String[] args) {
    SpringApplication.run(CompetitionApplication.class, args);
  }
}
