package com.odoru.memberservice.dto;

import com.odoru.memberservice.model.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payload for updating member profile details")
public class MemberUpdateRequest {

  @NotBlank(message = "Last name is required")
  @Schema(description = "Member's last name", example = "Doe")
  private String lastName;

  @NotBlank(message = "First name is required")
  @Schema(description = "Member's first name", example = "John")
  private String firstName;

  @Valid
  @Schema(description = "Residence address")
  private Address residenceAddress;
}
