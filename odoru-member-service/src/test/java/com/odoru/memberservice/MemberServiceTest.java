package com.odoru.memberservice;

import com.odoru.memberservice.dto.MemberCreateRequest;
import com.odoru.memberservice.dto.MemberUpdateRequest;
import com.odoru.memberservice.dto.RegistrationStatusDto;
import com.odoru.memberservice.exception.DuplicateFieldException;
import com.odoru.memberservice.exception.MemberNotFoundException;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    // --- registerMember ---

    @Test
    void registerMember_shouldSaveAndReturnMemberWithDefaultRole() {
        MemberCreateRequest request = MemberCreateRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .username("jdoe")
                .password("password")
                .residenceAddress(new Address("Toulouse", "France"))
                .build();

        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

        Member saved = memberService.registerMember(request);

        assertNotNull(saved);
        assertEquals(MemberRole.MEMBER, saved.getRole());
        assertEquals("John", saved.getFirstName());
        assertFalse(saved.isFeePaid());
        assertFalse(saved.isMedicalCertificateProvided());
        assertFalse(saved.isRegistrationValidated());
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void registerMember_shouldThrowWhenEmailAlreadyExists() {
        MemberCreateRequest request = MemberCreateRequest.builder()
                .email("taken@example.com")
                .username("jdoe")
                .build();

        when(memberRepository.findByEmail("taken@example.com"))
                .thenReturn(Optional.of(new Member()));

        assertThrows(DuplicateFieldException.class,
                () -> memberService.registerMember(request));
        verify(memberRepository, never()).save(any());
    }

    @Test
    void registerMember_shouldThrowWhenUsernameAlreadyExists() {
        MemberCreateRequest request = MemberCreateRequest.builder()
                .email("fresh@example.com")
                .username("taken")
                .build();

        when(memberRepository.findByEmail("fresh@example.com"))
                .thenReturn(Optional.empty());
        when(memberRepository.findByUsername("taken"))
                .thenReturn(Optional.of(new Member()));

        assertThrows(DuplicateFieldException.class,
                () -> memberService.registerMember(request));
        verify(memberRepository, never()).save(any());
    }

    // --- getMemberById ---

    @Test
    void getMemberById_shouldReturnMemberWhenExists() {
        Member member = Member.builder().id("abc").firstName("Jane").build();
        when(memberRepository.findById("abc")).thenReturn(Optional.of(member));

        Member result = memberService.getMemberById("abc");

        assertEquals("Jane", result.getFirstName());
    }

    @Test
    void getMemberById_shouldThrowWhenNotFound() {
        when(memberRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class,
                () -> memberService.getMemberById("missing"));
    }

    // --- updateMember ---

    @Test
    void updateMember_shouldUpdateAllowedFields() {
        Member existing = Member.builder()
                .id("abc")
                .firstName("Old")
                .lastName("Name")
                .build();

        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .firstName("New")
                .lastName("Name2")
                .residenceAddress(new Address("Paris", "France"))
                .build();

        when(memberRepository.findById("abc")).thenReturn(Optional.of(existing));
        when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

        Member updated = memberService.updateMember("abc", request);

        assertEquals("New", updated.getFirstName());
        assertEquals("Name2", updated.getLastName());
        assertEquals("Paris", updated.getResidenceAddress().getCity());
    }

    // --- updateExpertiseLevel ---

    @Test
    void updateExpertiseLevel_shouldUpdateWhenValid() {
        Member member = Member.builder().id("abc").expertiseLevel(1).build();

        when(memberRepository.findById("abc")).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

        Member updated = memberService.updateExpertiseLevel("abc", 3);

        assertEquals(3, updated.getExpertiseLevel());
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void updateExpertiseLevel_shouldRejectOutOfRange() {
        assertThrows(IllegalArgumentException.class,
                () -> memberService.updateExpertiseLevel("any", 6));
        assertThrows(IllegalArgumentException.class,
                () -> memberService.updateExpertiseLevel("any", 0));
        verify(memberRepository, never()).findById(anyString());
    }

    // --- deleteMember ---

    @Test
    void deleteMember_shouldDeleteWhenExists() {
        Member member = Member.builder().id("abc").build();
        when(memberRepository.findById("abc")).thenReturn(Optional.of(member));

        memberService.deleteMember("abc");

        verify(memberRepository).deleteById("abc");
    }

    @Test
    void deleteMember_shouldThrowWhenNotFound() {
        when(memberRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class,
                () -> memberService.deleteMember("missing"));
        verify(memberRepository, never()).deleteById(anyString());
    }

    // --- updateRegistrationStatus ---

    @Test
    void updateRegistrationStatus_shouldUpdateAllFields() {
        Member member = Member.builder()
                .id("abc")
                .feePaid(false)
                .medicalCertificateProvided(false)
                .registrationValidated(false)
                .build();

        RegistrationStatusDto dto = RegistrationStatusDto.builder()
                .feePaid(true)
                .medicalCertificateProvided(true)
                .registrationValidated(true)
                .build();

        when(memberRepository.findById("abc")).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

        Member updated = memberService.updateRegistrationStatus("abc", dto);

        assertTrue(updated.isFeePaid());
        assertTrue(updated.isMedicalCertificateProvided());
        assertTrue(updated.isRegistrationValidated());
    }

    @Test
    void updateRegistrationStatus_shouldSupportPartialUpdate() {
        Member member = Member.builder()
                .id("abc")
                .feePaid(true)
                .medicalCertificateProvided(false)
                .registrationValidated(false)
                .build();

        RegistrationStatusDto dto = RegistrationStatusDto.builder()
                .registrationValidated(true)
                .build();

        when(memberRepository.findById("abc")).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

        Member updated = memberService.updateRegistrationStatus("abc", dto);

        assertTrue(updated.isFeePaid());
        assertFalse(updated.isMedicalCertificateProvided());
        assertTrue(updated.isRegistrationValidated());
    }

    // --- getAllMembers ---

    @Test
    void getAllMembers_shouldReturnAllMembers() {
        when(memberRepository.findAll()).thenReturn(
                List.of(Member.builder().id("a").build(),
                        Member.builder().id("b").build()));

        List<Member> members = memberService.getAllMembers();

        assertEquals(2, members.size());
    }
}
