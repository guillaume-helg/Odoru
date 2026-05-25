package com.odoru.statsservice.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object representing a competition from Competition Service.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitionDto {

  /** The unique identifier of the competition. */
  private String id;

  /** The title of the competition. */
  private String title;

  /** The target level of the competition. */
  private int targetLevel;

  /** The date and time when the competition starts. */
  private LocalDateTime dateTime;

  /** The duration of the competition in minutes. */
  private int duration;

  /** The identifier of the teacher. */
  private String teacherId;

  /** The location where the competition takes place. */
  private String location;
}
