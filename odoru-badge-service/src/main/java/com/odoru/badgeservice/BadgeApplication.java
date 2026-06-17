package com.odoru.badgeservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Odoru Badge Service API",
        version = "1.0",
        description = "Badge Management & Attendance Tracking API"
    )
)
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class BadgeApplication {

    public static void main(final String[] args) {
        SpringApplication.run(BadgeApplication.class, args);
    }
}
