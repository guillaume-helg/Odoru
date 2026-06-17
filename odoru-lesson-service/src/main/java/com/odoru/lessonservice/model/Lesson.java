package com.odoru.lessonservice.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {

  private static final int MAX_TARGET_LEVEL = 5;

  @Id
  private String id;

  @NotBlank(message = "Title is required")
  private String title;

  @Min(value = 1, message = "Target level must be at least 1")
  @Max(value = MAX_TARGET_LEVEL, message = "Target level must be at most 5")
  private int targetLevel;

  @NotNull(message = "Date and time are required")
  private LocalDateTime dateTime;

  @Min(value = 1, message = "Duration must be at least 1 minute")
  private int duration;

  @NotBlank(message = "Teacher ID is required")
  private String teacherId;

  @NotBlank(message = "Location is required")
  private String location;
}
