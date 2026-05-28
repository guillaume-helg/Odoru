package com.odoru.memberservice.service;

import com.odoru.memberservice.dto.MemberCreateRequest;
import com.odoru.memberservice.dto.MemberUpdateRequest;
import com.odoru.memberservice.dto.RegistrationStatusDto;
import com.odoru.memberservice.exception.DuplicateFieldException;
import com.odoru.memberservice.exception.MemberNotFoundException;
import com.odoru.memberservice.model.Member;
import com.odoru.memberservice.model.MemberRole;
import com.odoru.memberservice.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Business logic for member registration, updates, and validations. */
@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public List<Member> getAllMembers() {
    return memberRepository.findAll();
  }

  /**
   * @throws MemberNotFoundException if no member exists with given id
   */
  public Member getMemberById(final String id) {
    return memberRepository.findById(id)
        .orElseThrow(() -> new MemberNotFoundException(id));
  }

  /**
   * Registers a new member from a create request.
   *
   * @throws DuplicateFieldException if email or username already taken
   */
  public Member registerMember(final MemberCreateRequest request) {
    if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new DuplicateFieldException("Email");
    }
    if (memberRepository.findByUsername(request.getUsername())
        .isPresent()) {
      throw new DuplicateFieldException("Username");
    }

    Member member = Member.builder()
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .email(request.getEmail())
        .username(request.getUsername())
        .password(passwordEncoder.encode(request.getPassword()))
        .residenceAddress(request.getResidenceAddress())
        .role(MemberRole.MEMBER)
        .build();

    return memberRepository.save(member);
  }

  /** Updates only name and address fields. */
  public Member updateMember(final String id,
      final MemberUpdateRequest request) {
    Member member = getMemberById(id);
    member.setFirstName(request.getFirstName());
    member.setLastName(request.getLastName());
    member.setResidenceAddress(request.getResidenceAddress());
    return memberRepository.save(member);
  }

  /**
   * @throws IllegalArgumentException if level is outside 1-5 range
   */
  public Member updateExpertiseLevel(final String id,
      final int newLevel) {
    if (newLevel < 1 || newLevel > Member.MAX_EXPERTISE_LEVEL) {
      throw new IllegalArgumentException(
          "Expertise level must be between 1 and "
              + Member.MAX_EXPERTISE_LEVEL);
    }
    Member member = getMemberById(id);
    member.setExpertiseLevel(newLevel);
    return memberRepository.save(member);
  }

  /**
   * @throws MemberNotFoundException if member does not exist
   */
  public void deleteMember(final String id) {
    getMemberById(id);
    memberRepository.deleteById(id);
  }

  /** Partial update of registration flags (fee, certificate, validation). */
  public Member updateRegistrationStatus(final String id,
      final RegistrationStatusDto dto) {
    Member member = getMemberById(id);
    if (dto.getFeePaid() != null) {
      member.setFeePaid(dto.getFeePaid());
    }
    if (dto.getMedicalCertificateProvided() != null) {
      member.setMedicalCertificateProvided(dto.getMedicalCertificateProvided());
    }
    if (dto.getRegistrationValidated() != null) {
      member.setRegistrationValidated(dto.getRegistrationValidated());
    }
    return memberRepository.save(member);
  }

  public Member updateMemberRole(final String id,
      final MemberRole role) {
    Member member = getMemberById(id);
    member.setRole(role);
    return memberRepository.save(member);
  }
}
