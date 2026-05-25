package com.odoru.statsservice.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO detailing the list and count of students present at a given lesson.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonAttendanceDto {

  /** The total number of students present. */
  private int presentCount;

  /** The list of member profiles of the present students. */
  private List<MemberDto> presentStudents;
}
