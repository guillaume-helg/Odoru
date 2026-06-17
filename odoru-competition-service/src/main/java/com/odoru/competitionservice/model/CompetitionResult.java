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

@Document(collection = "competition_results")
@CompoundIndex(def = "{'competitionId': 1, 'studentId': 1}", unique = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitionResult {

    private static final long MAX_SCORE_LIMIT = 10L;

    @Id
    private String id;

    @NotBlank(message = "Competition ID is required")
    private String competitionId;

    @NotBlank(message = "Student ID is required")
    private String studentId;

    @NotNull(message = "Score is required")
    @Min(value = 0, message = "Score must be at least 0.0")
    @Max(value = MAX_SCORE_LIMIT, message = "Score must be at most 10.0")
    private Double score;

    @NotBlank(message = "Teacher ID is required")
    private String teacherId;
}
