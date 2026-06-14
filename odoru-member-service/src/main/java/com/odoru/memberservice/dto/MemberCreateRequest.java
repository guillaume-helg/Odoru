package com.odoru.memberservice.dto;

import com.odoru.memberservice.model.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for new member registration. Exposes only the fields
 * a user is allowed to set during signup — role, fees, and
 * administrative flags are controlled server-side.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payload for registering a new member")
public class MemberCreateRequest {

  @NotBlank(message = "Last name is required")
  @Schema(description = "Member's last name", example = "Doe")
  private String lastName;

  @NotBlank(message = "First name is required")
  @Schema(description = "Member's first name", example = "John")
  private String firstName;

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @Schema(description = "Member's email address",
      example = "john.doe@example.com")
  private String email;

  @NotBlank(message = "Username is required")
  @Schema(description = "Desired username", example = "jdoe")
  private String username;

  @NotBlank(message = "Password is required")
  @Schema(description = "Password (will be hashed before storage)",
      example = "s3cur3P@ss")
  private String password;

  @Min(value = 1, message = "Expertise level must be at least 1")
  @Max(value = 5, message = "Expertise level must be at most 5")
  @Schema(description = "Expertise level (1-5)", example = "1")
  private Integer expertiseLevel;

  @Valid
  @Schema(description = "Residence address")
  private Address residenceAddress;
}
