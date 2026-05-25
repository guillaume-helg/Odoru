package com.odoru.memberservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for partial updates of a member's
 * registration status.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO for updating a member's registration status")
public class RegistrationStatusDto {

  /** Indicates whether the membership fee has been paid. */
  @Schema(
      description = "Indicates whether the membership fee has been paid",
      example = "true"
  )
  private Boolean feePaid;

  /** Indicates whether the medical certificate has been provided. */
  @Schema(description = "Medical cert provided status", example = "true")
  private Boolean medicalCertificateProvided;

  /** Indicates whether the registration has been validated. */
  @Schema(
      description = "Indicates whether the registration has been validated",
      example = "true"
  )
  private Boolean registrationValidated;
}
