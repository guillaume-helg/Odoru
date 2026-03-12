package com.odoru.memberservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/** Dance club member entity with registration details. */
@Document(collection = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

  public static final int MAX_EXPERTISE_LEVEL = 5;

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
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  @Valid
  private Address residenceAddress;

  @Min(value = 1, message = "Expertise level must be at least 1")
  @Max(value = MAX_EXPERTISE_LEVEL,
      message = "Expertise level must be at most 5")
  @Builder.Default
  private Integer expertiseLevel = 1;

  private MemberRole role;

  @Builder.Default
  private boolean feePaid = false;

  @Builder.Default
  private boolean medicalCertificateProvided = false;

  @Builder.Default
  private boolean registrationValidated = false;
}
