package com.odoru.statsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object representing a competition result from Competition Service.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitionResultDto {

  /** The unique identifier of the result entry. */
  private String id;

  /** The unique identifier of the competition. */
  private String competitionId;

  /** The unique identifier of the student. */
  private String studentId;

  /** The score achieved by the student (0.0 - 10.0). */
  private Double score;

  /** The unique identifier of the teacher who entered this result. */
  private String teacherId;
}
