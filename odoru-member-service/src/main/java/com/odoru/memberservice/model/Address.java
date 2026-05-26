package com.odoru.memberservice.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Value object for a member's residence address. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

  /**
   * The city where the member resides.
   */
  @NotBlank(message = "City is required")
  private String city;

  /**
   * The country where the member resides.
   */
  @NotBlank(message = "Country is required")
  private String country;
}
