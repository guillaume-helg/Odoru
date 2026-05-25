package com.odoru.statsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO representing a student's competition result (with their optional score).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCompetitionResultDto {

  /** The competition details. */
  private CompetitionDto competition;

  /** The score achieved by the student (null if not yet graded). */
  private Double score;
}
