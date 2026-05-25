package com.odoru.statsservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Spring Boot Stats Application service.
 */
@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Odoru Stats Service API",
        version = "1.0",
        description = "President Statistics Aggregation API"
    )
)
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class StatsApplication {

  /**
   * Main entry point running the Spring Application context.
   *
   * @param args the command-line arguments
   */
  public static void main(final String[] args) {
    SpringApplication.run(StatsApplication.class, args);
  }
}
