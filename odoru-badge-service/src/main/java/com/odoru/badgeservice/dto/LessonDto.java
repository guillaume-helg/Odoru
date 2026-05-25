package com.odoru.badgeservice.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object representing a lesson/course slot from Lesson Service.
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

  /** The target level of the lesson. */
  private int targetLevel;

  /** The date and time when the lesson starts. */
  private LocalDateTime dateTime;

  /** The duration of the lesson in minutes. */
  private int duration;

  /** The identifier of the teacher. */
  private String teacherId;

  /** The location where the lesson takes place. */
  private String location;
}
