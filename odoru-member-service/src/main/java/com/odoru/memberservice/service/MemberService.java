package com.odoru.memberservice.service;

import com.odoru.memberservice.dto.RegistrationStatusDto;
import com.odoru.memberservice.model.Member;
import com.odoru.memberservice.model.MemberRole;
import com.odoru.memberservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMemberById(String id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id : " + id));
    }

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

    public Member updateMember(String id, Member memberDetails) {
        Member member = getMemberById(id);
        member.setFirstName(memberDetails.getFirstName());
        member.setLastName(memberDetails.getLastName());
        member.setResidenceAddress(memberDetails.getResidenceAddress());
        return memberRepository.save(member);
    }

    public Member updateExpertiseLevel(String id, int newLevel) {
        if (newLevel < 1 || newLevel > 5) {
            throw new IllegalArgumentException("Expertise level between 1 and 5");
        }
        Member member = getMemberById(id);
        member.setExpertiseLevel(newLevel);
        return memberRepository.save(member);
    }

    public void deleteMember(String id) {
        memberRepository.deleteById(id);
    }

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
