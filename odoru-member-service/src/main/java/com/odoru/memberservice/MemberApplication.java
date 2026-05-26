package com.odoru.memberservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Entry point for the Odoru Member Service. */
@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Odoru Member Service API",
        version = "1.0",
        description = "Member Management API"
    )
)
public final class MemberApplication {

  public static void main(final String[] args) {
    SpringApplication.run(MemberApplication.class, args);
  }
}
