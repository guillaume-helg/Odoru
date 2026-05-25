package com.odoru.memberservice.controller;

import com.odoru.memberservice.dto.MemberCreateRequest;
import com.odoru.memberservice.dto.MemberResponse;
import com.odoru.memberservice.dto.MemberUpdateRequest;
import com.odoru.memberservice.dto.RegistrationStatusDto;
import com.odoru.memberservice.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

/**
 * REST endpoints for member administration and self-registration.
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Members",
    description = "Member management, self-registration, "
        + "and administrative controls")
public final class MemberController {

  private final MemberService memberService;

  @GetMapping
  @Operation(summary = "Get all members")
  public ResponseEntity<List<MemberResponse>> getAllMembers() {
    List<MemberResponse> members = memberService.getAllMembers()
        .stream()
        .map(MemberResponse::from)
        .toList();
    return ResponseEntity.ok(members);
  }

  @GetMapping("/{id}")
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
  @Operation(summary = "Update member profile",
      description = "Updates name and address only")
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

  /**
   * Enables updating a member's role (e.g., designating them as a teacher).
   *
   * @param id the unique identifier of the member
   * @param role the new role to assign
   * @return the updated member profile
   */
  @PatchMapping("/{id}/role")
  @Operation(
      summary = "Update member role",
      description = "Enables updating a member's role (e.g., designating "
          + "them as a teacher). Restricted to Secretary."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Member role successfully updated"),
      @ApiResponse(responseCode = "400",
          description = "Invalid role value"),
      @ApiResponse(responseCode = "404",
          description = "Member not found with the specified ID")
  })
  public ResponseEntity<Member> updateRole(
      @Parameter(description = "The unique identifier of the member",
          required = true)
      @PathVariable final String id,
      @Parameter(description = "The new role to assign", required = true)
      @RequestParam final MemberRole role) {
    return ResponseEntity.ok(memberService.updateMemberRole(id, role));
  }
}

