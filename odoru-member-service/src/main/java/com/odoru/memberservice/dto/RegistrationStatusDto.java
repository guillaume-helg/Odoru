package com.odoru.memberservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Partial update of a member's registration status flags. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO for updating a member's registration status")
public class RegistrationStatusDto {

  @Schema(description = "Whether the membership fee has been paid",
      example = "true")
  private Boolean feePaid;

  @Schema(description = "Whether a medical certificate was provided",
      example = "true")
  private Boolean medicalCertificateProvided;

  @Schema(description = "Whether the registration has been validated",
      example = "true")
  private Boolean registrationValidated;
}
