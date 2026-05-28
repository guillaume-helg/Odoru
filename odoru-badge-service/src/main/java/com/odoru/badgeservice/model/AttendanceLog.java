package com.odoru.badgeservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entity logging the presence of a student at a course/lesson session.
 */
@Document(collection = "attendance_logs")
@CompoundIndex(def = "{'memberId': 1, 'lessonId': 1}", unique = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceLog {

  @Id
  private String id;

  @NotBlank(message = "Member ID is required")
  private String memberId;

  @NotBlank(message = "Lesson ID is required")
  private String lessonId;

  @NotNull(message = "Timestamp is required")
  private LocalDateTime timestamp;
}
