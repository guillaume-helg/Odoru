package com.odoru.memberservice.model;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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
