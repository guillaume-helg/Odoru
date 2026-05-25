package com.odoru.memberservice.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entity representing a dance club member and their registration details.
 */
@Document(collection = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

  @Id
  private String id;

  @NotBlank(message = "Last name is required")
  private String lastName;

  @NotBlank(message = "First name is required")
  private String firstName;

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @Indexed(unique = true)
  private String email;

  @NotBlank(message = "Username is required")
  @Indexed(unique = true)
  private String username;

  @NotBlank(message = "Password is required")
  private String password;

  private Address residenceAddress;

  @Min(value = 1, message = "Expertise level must be at least 1")
  @Max(value = 5, message = "Expertise level must be at most 5")
  private int expertiseLevel;

  private MemberRole role;

  @Builder.Default
  private boolean feePaid = false;

  @Builder.Default
  private boolean medicalCertificateProvided = false;

  @Builder.Default
  private boolean registrationValidated = false;
}
