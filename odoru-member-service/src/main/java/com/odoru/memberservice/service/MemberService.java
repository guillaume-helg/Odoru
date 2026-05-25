package com.odoru.memberservice.service;

import java.util.List;
import com.odoru.memberservice.dto.RegistrationStatusDto;
import com.odoru.memberservice.model.Member;
import com.odoru.memberservice.model.MemberRole;
import com.odoru.memberservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class handling business logic for member registration, updates, and validations.
 */
@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;

  /**
   * Retrieves all registered members from the repository.
   *
   * @return the list of all members
   */
  public List<Member> getAllMembers() {
    return memberRepository.findAll();
  }

  /**
   * Finds a member by their ID. Throws an exception if the member does not exist.
   *
   * @param id the unique identifier of the member
   * @return the member entity
   * @throws RuntimeException if the member is not found
   */
  public Member getMemberById(String id) {
    return memberRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Member not found with id : " + id));
  }

  /**
   * Registers a new member, checking for email and username uniqueness.
   *
   * @param member the member details to register
   * @return the saved member entity
   * @throws RuntimeException if the email or username is already in use
   */
  public Member registerMember(Member member) {
    if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
      throw new RuntimeException("Email already in use");
    }
    if (memberRepository.findByUsername(member.getUsername()).isPresent()) {
      throw new RuntimeException("Username already in use");
    }
    if (member.getRole() == null) {
      member.setRole(MemberRole.MEMBER);
    }
    return memberRepository.save(member);
  }

  /**
   * Updates basic member profile details.
   *
   * @param id the unique identifier of the member to update
   * @param memberDetails the new profile details
   * @return the updated and saved member entity
   */
  public Member updateMember(String id, Member memberDetails) {
    Member member = getMemberById(id);
    member.setFirstName(memberDetails.getFirstName());
    member.setLastName(memberDetails.getLastName());
    member.setResidenceAddress(memberDetails.getResidenceAddress());
    return memberRepository.save(member);
  }

  /**
   * Updates the expertise level of a member (must be between 1 and 5).
   *
   * @param id the unique identifier of the member
   * @param newLevel the new expertise level
   * @return the updated and saved member entity
   * @throws IllegalArgumentException if the level is out of the 1-5 range
   */
  public Member updateExpertiseLevel(String id, int newLevel) {
    if (newLevel < 1 || newLevel > 5) {
      throw new IllegalArgumentException("Expertise level between 1 and 5");
    }
    Member member = getMemberById(id);
    member.setExpertiseLevel(newLevel);
    return memberRepository.save(member);
  }

  /**
   * Deletes a member record by their ID.
   *
   * @param id the unique identifier of the member to delete
   */
  public void deleteMember(String id) {
    memberRepository.deleteById(id);
  }

  /**
   * Updates specific registration validation flags (fee payment, certificate, validation status).
   *
   * @param id the unique identifier of the member
   * @param dto the status fields to update
   * @return the updated and saved member entity
   */
  public Member updateRegistrationStatus(String id, RegistrationStatusDto dto) {
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
}
