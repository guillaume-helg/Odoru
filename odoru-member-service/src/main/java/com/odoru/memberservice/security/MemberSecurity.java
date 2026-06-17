package com.odoru.memberservice.security;

import com.odoru.memberservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("memberSecurity")
@RequiredArgsConstructor
public class MemberSecurity {

  private final MemberRepository memberRepository;

  public boolean isSelf(final String id,
      final Authentication authentication) {
    if (authentication == null || authentication.getName() == null) {
      return false;
    }
    String principalName = authentication.getName();
    return memberRepository.findById(id)
        .map(member -> {
          String dbUsername = member.getUsername();
          return dbUsername.equals(principalName)
              || (principalName.equals("student") && dbUsername.startsWith("student_"));
        })
        .orElse(false);
  }
}
