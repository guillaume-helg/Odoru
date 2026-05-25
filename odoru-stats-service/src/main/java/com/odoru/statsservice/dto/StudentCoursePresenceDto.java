package com.odoru.statsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO representing a student's course attendance status (present or absent).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCoursePresenceDto {

  /** The course/lesson details. */
  private LessonDto lesson;

  /** Whether the student was present (attended/swiped) at this lesson. */
  private boolean present;
}
