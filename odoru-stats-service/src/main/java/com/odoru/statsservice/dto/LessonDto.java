package com.odoru.statsservice.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object representing a course/lesson slot from Lesson Service.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonDto {

  /** The unique identifier of the lesson. */
  private String id;

  /** The title of the lesson. */
  private String title;

  /** The target expertise level of the lesson. */
  private int targetLevel;

  /** The start date and time of the lesson. */
  private LocalDateTime dateTime;

  /** The duration of the lesson in minutes. */
  private int duration;

  /** The unique identifier of the teacher. */
  private String teacherId;

  /** The location where the lesson takes place. */
  private String location;
}
