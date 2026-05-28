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

  private String id;
  private String title;
  private int targetLevel;
  private LocalDateTime dateTime;
  private int duration;
  private String teacherId;
  private String location;
}
