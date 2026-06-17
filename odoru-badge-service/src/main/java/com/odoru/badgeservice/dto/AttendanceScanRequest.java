package com.odoru.badgeservice.dto;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceScanRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Badge number is mandatory")
    private String badgeNumber;

    @NotBlank(message = "Lesson ID is mandatory")
    private String lessonId;
}
