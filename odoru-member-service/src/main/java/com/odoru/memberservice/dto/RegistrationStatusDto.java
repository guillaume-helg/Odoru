package com.odoru.memberservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for partial updates of a member's registration status.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object for updating a member's registration status")
public class RegistrationStatusDto {

  @Schema(description = "Indicates whether the membership fee has been paid", example = "true")
  private Boolean feePaid;

  @Schema(description = "Indicates whether the medical certificate has been provided", example = "true")
  private Boolean medicalCertificateProvided;

  @Schema(description = "Indicates whether the registration has been validated by the secretary", example = "true")
  private Boolean registrationValidated;
}
