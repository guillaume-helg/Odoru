package com.odoru.memberservice.controller;

import com.odoru.memberservice.dto.MemberCreateRequest;
import com.odoru.memberservice.dto.MemberResponse;
import com.odoru.memberservice.dto.MemberUpdateRequest;
import com.odoru.memberservice.dto.RegistrationStatusDto;
import com.odoru.memberservice.model.MemberRole;
import com.odoru.memberservice.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "Member Profile & Registration APIs")
@Slf4j
public class MemberController {

  private final MemberService memberService;

  @GetMapping
  @PreAuthorize("hasAnyRole('SECRETARY', 'PRESIDENT', 'TEACHER')")
  @Operation(summary = "Get members with optional filters",
      description = "Retrieve all members or filter by registration status.")
  public ResponseEntity<List<MemberResponse>> getAllMembers(
      @Parameter(description = "Filter by fee payment status")
      @RequestParam(required = false) final Boolean feePaid,
      @Parameter(description = "Filter by medical certificate status")
      @RequestParam(required = false)
      final Boolean medicalCertificateProvided,
      @Parameter(description = "Filter by registration validation status")
      @RequestParam(required = false) final Boolean registrationValidated) {
    log.debug("Fetching members with filters: feePaid={}, medicalCert={}, validated={}",
        feePaid, medicalCertificateProvided, registrationValidated);
    List<MemberResponse> members = memberService.getAllMembers(
            feePaid, medicalCertificateProvided, registrationValidated)
        .stream()
        .map(MemberResponse::from)
        .toList();
    return ResponseEntity.ok(members);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('SECRETARY', 'PRESIDENT', 'TEACHER') "
      + "or @memberSecurity.isSelf(#id, authentication)")
  @Operation(summary = "Get member by ID")
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Member found"),
      @ApiResponse(responseCode = "404",
          description = "Member not found")
  })
  public ResponseEntity<MemberResponse> getMemberById(
      @Parameter(description = "Member ID", required = true)
      @PathVariable final String id) {
    return ResponseEntity.ok(
        MemberResponse.from(memberService.getMemberById(id)));
  }

  @PostMapping("/signup")
  @Operation(summary = "Register a new member",
      description = "Self-registration — defaults to MEMBER role")
  @ApiResponses({
      @ApiResponse(responseCode = "201",
          description = "Member registered"),
      @ApiResponse(responseCode = "400",
          description = "Invalid input"),
      @ApiResponse(responseCode = "409",
          description = "Email or username already exists")
  })
  public ResponseEntity<MemberResponse> signup(
      @Valid @RequestBody final MemberCreateRequest request) {
    return new ResponseEntity<>(
        MemberResponse.from(memberService.registerMember(request)),
        HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('SECRETARY', 'PRESIDENT') "
      + "or @memberSecurity.isSelf(#id, authentication)")
  @Operation(summary = "Update member profile",
      description = "Updates name and address only — self or secretary/president")
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Member updated"),
      @ApiResponse(responseCode = "404",
          description = "Member not found")
  })
  public ResponseEntity<MemberResponse> updateMember(
      @Parameter(description = "Member ID", required = true)
      @PathVariable final String id,
      @Valid @RequestBody final MemberUpdateRequest request) {
    return ResponseEntity.ok(
        MemberResponse.from(memberService.updateMember(id, request)));
  }

  @PatchMapping("/{id}/expertise")
  @PreAuthorize("hasAnyRole('SECRETARY', 'PRESIDENT')")
  @Operation(summary = "Update expertise level (1-5)")
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Level updated"),
      @ApiResponse(responseCode = "400",
          description = "Invalid level"),
      @ApiResponse(responseCode = "404",
          description = "Member not found")
  })
  public ResponseEntity<MemberResponse> updateExpertise(
      @Parameter(description = "Member ID", required = true)
      @PathVariable final String id,
      @Parameter(description = "Level (1-5)", required = true)
      @RequestParam final int level) {
    return ResponseEntity.ok(
        MemberResponse.from(
            memberService.updateExpertiseLevel(id, level)));
  }

  @PatchMapping("/{id}/registration-status")
  @PreAuthorize("hasAnyRole('SECRETARY', 'PRESIDENT')")
  @Operation(summary = "Update registration status",
      description = "Update payment, certificate, and validation flags")
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Status updated"),
      @ApiResponse(responseCode = "404",
          description = "Member not found")
  })
  public ResponseEntity<MemberResponse> updateRegistrationStatus(
      @Parameter(description = "Member ID", required = true)
      @PathVariable final String id,
      @Valid @RequestBody
      final RegistrationStatusDto registrationStatusDto) {
    return ResponseEntity.ok(
        MemberResponse.from(
            memberService.updateRegistrationStatus(id,
                registrationStatusDto)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('SECRETARY', 'PRESIDENT')")
  @Operation(summary = "Delete a member")
  @ApiResponses({
      @ApiResponse(responseCode = "204",
          description = "Member deleted"),
      @ApiResponse(responseCode = "404",
          description = "Member not found")
  })
  public ResponseEntity<Void> deleteMember(
      @Parameter(description = "Member ID", required = true)
      @PathVariable final String id) {
    memberService.deleteMember(id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/role")
  @PreAuthorize("hasAnyRole('SECRETARY', 'PRESIDENT')")
  @Operation(summary = "Update member role")
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Member role successfully updated"),
      @ApiResponse(responseCode = "400",
          description = "Invalid role value"),
      @ApiResponse(responseCode = "404",
          description = "Member not found with the specified ID")
  })
  public ResponseEntity<MemberResponse> updateRole(
      @Parameter(description = "The unique identifier of the member",
          required = true)
      @PathVariable final String id,
      @Parameter(description = "The new role to assign", required = true)
      @RequestParam final MemberRole role) {
    return ResponseEntity.ok(
        MemberResponse.from(memberService.updateMemberRole(id, role)));
  }
}
