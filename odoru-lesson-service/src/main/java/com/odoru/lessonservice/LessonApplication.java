package com.odoru.lessonservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Odoru Lesson Service API",
        version = "1.0",
        description = "Lesson/Course Slots Management API"
    )
)
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class LessonApplication {

  public static void main(final String[] args) {
    SpringApplication.run(LessonApplication.class, args);
  }
}
