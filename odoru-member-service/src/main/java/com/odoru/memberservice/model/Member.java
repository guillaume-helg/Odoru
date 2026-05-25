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

  /** Maximum expertise level constant. */
  private static final int MAX_EXPERTISE_LEVEL = 5;

  /** The unique identifier for the member. */
  @Id
  private String id;

  /** The member's last name. */
  @NotBlank(message = "Last name is required")
  private String lastName;

  /** The member's first name. */
  @NotBlank(message = "First name is required")
  private String firstName;

  /** The member's email address. */
  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @Indexed(unique = true)
  private String email;

  /** The member's username. */
  @NotBlank(message = "Username is required")
  @Indexed(unique = true)
  private String username;

  /** The member's password. */
  @NotBlank(message = "Password is required")
  private String password;

  /** The member's residence address. */
  private Address residenceAddress;

  /** The member's dance expertise level (1-5). */
  @Min(value = 1, message = "Expertise level must be at least 1")
  @Max(value = MAX_EXPERTISE_LEVEL,
      message = "Expertise level must be at most 5")
  private int expertiseLevel;

  /** The member's role. */
  private MemberRole role;

  /** Whether the member has paid their fee. */
  @Builder.Default
  private boolean feePaid = false;

  /** Whether the member provided a medical certificate. */
  @Builder.Default
  private boolean medicalCertificateProvided = false;

  /** Whether the member's registration has been validated. */
  @Builder.Default
  private boolean registrationValidated = false;
}
