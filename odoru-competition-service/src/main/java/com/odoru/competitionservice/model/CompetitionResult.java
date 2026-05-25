package com.odoru.competitionservice.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entity representing a student's result/score in a rhythmic dance competition.
 */
@Document(collection = "competition_results")
@CompoundIndex(def = "{'competitionId': 1, 'studentId': 1}", unique = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitionResult {

  /** Maximum score allowed (out of 10). */
  private static final double MAX_SCORE = 10.0;

  /** The unique identifier of the result entry. */
  @Id
  private String id;

  /** The unique identifier of the competition. */
  @NotBlank(message = "Competition ID is required")
  private String competitionId;

  /** The unique identifier of the student. */
  @NotBlank(message = "Student ID is required")
  private String studentId;

  /** The score achieved by the student (0.0 - 10.0). */
  @NotNull(message = "Score is required")
  @Min(value = 0, message = "Score must be at least 0.0")
  @Max(value = 10, message = "Score must be at most 10.0")
  private Double score;

  /** The unique identifier of the teacher who entered this result. */
  @NotBlank(message = "Teacher ID is required")
  private String teacherId;
}
