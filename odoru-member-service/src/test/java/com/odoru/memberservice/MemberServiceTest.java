package com.odoru.memberservice;

import com.odoru.memberservice.dto.RegistrationStatusDto;
import com.odoru.memberservice.model.Address;
import com.odoru.memberservice.model.Member;
import com.odoru.memberservice.model.MemberRole;
import com.odoru.memberservice.repository.MemberRepository;
import com.odoru.memberservice.service.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    void testRegisterMember_Success() {
        Member member = Member.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .username("jdoe")
                .password("password")
                .residenceAddress(new Address("Toulouse", "France"))
                .build();

        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member savedMember = memberService.registerMember(member);

        assertNotNull(savedMember);
        assertEquals(MemberRole.MEMBER, savedMember.getRole());
        assertFalse(savedMember.isFeePaid());
        assertFalse(savedMember.isMedicalCertificateProvided());
        assertFalse(savedMember.isRegistrationValidated());
        verify(memberRepository, times(1)).save(member);
    }

    @Test
    void testUpdateExpertiseLevel_Success() {
        Member member = new Member();
        member.setId("mongo-id-123");
        member.setExpertiseLevel(1);

        when(memberRepository.findById("mongo-id-123")).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Member updatedMember = memberService.updateExpertiseLevel("mongo-id-123", 3);

        assertEquals(3, updatedMember.getExpertiseLevel());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void testUpdateExpertiseLevel_InvalidRange() {
        assertThrows(IllegalArgumentException.class, () -> memberService.updateExpertiseLevel("any-id", 6));
        assertThrows(IllegalArgumentException.class, () -> memberService.updateExpertiseLevel("any-id", 0));

        verify(memberRepository, never()).findById(anyString());
    }

    @Test
    void testUpdateRegistrationStatus_Success() {
        Member member = new Member();
        member.setId("mongo-id-123");
        member.setFeePaid(false);
        member.setMedicalCertificateProvided(false);
        member.setRegistrationValidated(false);

        RegistrationStatusDto dto = RegistrationStatusDto.builder()
                .feePaid(true)
                .medicalCertificateProvided(true)
                .registrationValidated(true)
                .build();

        when(memberRepository.findById("mongo-id-123")).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Member updatedMember = memberService.updateRegistrationStatus("mongo-id-123", dto);

        assertTrue(updatedMember.isFeePaid());
        assertTrue(updatedMember.isMedicalCertificateProvided());
        assertTrue(updatedMember.isRegistrationValidated());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void testUpdateRegistrationStatus_PartialUpdate() {
        Member member = new Member();
        member.setId("mongo-id-123");
        member.setFeePaid(true);
        member.setMedicalCertificateProvided(false);
        member.setRegistrationValidated(false);

        // Update validation status only, keeping others as is
        RegistrationStatusDto dto = RegistrationStatusDto.builder()
                .registrationValidated(true)
                .build();

        when(memberRepository.findById("mongo-id-123")).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Member updatedMember = memberService.updateRegistrationStatus("mongo-id-123", dto);

        assertTrue(updatedMember.isFeePaid()); // unchanged
        assertFalse(updatedMember.isMedicalCertificateProvided()); // unchanged
        assertTrue(updatedMember.isRegistrationValidated()); // updated
        verify(memberRepository, times(1)).save(any(Member.class));
    }
}
