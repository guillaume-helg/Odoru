package com.odoru.statsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO summarizing total courses and average student attendance.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSummaryDto {

  /** The total number of courses/lessons scheduled. */
  private int totalCourses;

  /** The average number of students present per course. */
  private double averageAttendance;
}
