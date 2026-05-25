package com.odoru.lessonservice.dto;

import com.odoru.lessonservice.model.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object representing a member retrieved from Member Service.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {

  /** The unique identifier of the member. */
  private String id;

  /** The first name of the member. */
  private String firstName;

  /** The last name of the member. */
  private String lastName;

  /** The role assigned to the member. */
  private MemberRole role;

  /** The dance expertise level of the member. */
  private int expertiseLevel;
}
