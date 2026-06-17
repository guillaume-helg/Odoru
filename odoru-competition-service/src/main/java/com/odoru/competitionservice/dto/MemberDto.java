package com.odoru.competitionservice.dto;

import com.odoru.competitionservice.model.MemberRole;
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
public class MemberDto {

  private String id;
  private String firstName;
  private String lastName;
  private MemberRole role;
  private int expertiseLevel;
}
