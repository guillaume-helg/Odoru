package com.odoru.memberservice.dto;

import com.odoru.memberservice.model.Address;
import com.odoru.memberservice.model.Member;
import com.odoru.memberservice.model.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Read-only response DTO that excludes sensitive fields (password)
 * from API responses.
 */
@Getter
@AllArgsConstructor
@Builder
@Schema(description = "Member profile returned in API responses")
public class MemberResponse {

  @Schema(description = "Unique member identifier")
  private final String id;

  @Schema(description = "Last name")
  private final String lastName;

  @Schema(description = "First name")
  private final String firstName;

  @Schema(description = "Email address")
  private final String email;

  @Schema(description = "Username")
  private final String username;

  @Schema(description = "Residence address")
  private final Address residenceAddress;

  @Schema(description = "Dance expertise level (1-5)")
  private final Integer expertiseLevel;

  @Schema(description = "Role in the club")
  private final MemberRole role;

  @Schema(description = "Whether the membership fee has been paid")
  private final boolean feePaid;

  @Schema(description = "Whether a medical certificate was provided")
  private final boolean medicalCertificateProvided;

  @Schema(description = "Whether registration has been validated")
  private final boolean registrationValidated;

  /**
   * Maps a Member entity to its API response representation.
   *
   * @param member the entity to convert
   * @return a MemberResponse without sensitive data
   */
  public static MemberResponse from(final Member member) {
    return MemberResponse.builder()
        .id(member.getId())
        .lastName(member.getLastName())
        .firstName(member.getFirstName())
        .email(member.getEmail())
        .username(member.getUsername())
        .residenceAddress(member.getResidenceAddress())
        .expertiseLevel(member.getExpertiseLevel())
        .role(member.getRole())
        .feePaid(member.isFeePaid())
        .medicalCertificateProvided(
            member.isMedicalCertificateProvided())
        .registrationValidated(member.isRegistrationValidated())
        .build();
  }
}
