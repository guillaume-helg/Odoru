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

/**
 * Entity representing a scheduled lesson/course slot in the rhythmic dance club.
 */
@Document(collection = "lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {

  /** Maximum target expertise level constant. */
  private static final int MAX_TARGET_LEVEL = 5;

  /** The unique identifier for the lesson. */
  @Id
  private String id;

  /** The title of the lesson. */
  @NotBlank(message = "Title is required")
  private String title;

  /** The target expertise level (1-5). */
  @Min(value = 1, message = "Target level must be at least 1")
  @Max(value = MAX_TARGET_LEVEL, message = "Target level must be at most 5")
  private int targetLevel;

  /** The start date and time of the lesson. */
  @NotNull(message = "Date and time are required")
  private LocalDateTime dateTime;

  /** The duration of the lesson in minutes. */
  @Min(value = 1, message = "Duration must be at least 1 minute")
  private int duration;

  /** The unique identifier of the teacher teaching this lesson. */
  @NotBlank(message = "Teacher ID is required")
  private String teacherId;

  /** The location/venue of the lesson. */
  @NotBlank(message = "Location is required")
  private String location;
}
