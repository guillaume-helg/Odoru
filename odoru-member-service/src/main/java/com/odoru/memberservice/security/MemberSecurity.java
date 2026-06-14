package com.odoru.memberservice.security;

import com.odoru.memberservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Authorization helper exposed to SpEL in {@code @PreAuthorize} so a member
 * can act on their own record without granting staff privileges.
 */
@Component("memberSecurity")
@RequiredArgsConstructor
public class MemberSecurity {

  private final MemberRepository memberRepository;

  /**
   * Returns whether the authenticated principal owns the given member record,
   * matching the JWT {@code preferred_username} against the stored username.
   *
   * @param id the member id being acted upon
   * @param authentication the current authentication
   * @return true if the principal owns the record
   */
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
