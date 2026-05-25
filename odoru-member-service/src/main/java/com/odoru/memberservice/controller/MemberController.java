package com.odoru.memberservice.controller;

import com.odoru.memberservice.dto.RegistrationStatusDto;
import com.odoru.memberservice.model.Member;
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

/**
 * Controller class managing HTTP endpoints for member administration and
 * self-registration.
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(
    name = "Members",
    description = "APIs for managing members, self-registration, "
        + "and administrative controls."
)
public class MemberController {

  /** Member service dependency. */
  private final MemberService memberService;

  /**
   * Retrieves all registered members in the system.
   *
   * @return the list of members
   */
  @GetMapping
  @Operation(
      summary = "Get all members",
      description = "Retrieves a list of all registered members "
          + "in the system."
  )
  @ApiResponse(responseCode = "200",
      description = "Successfully retrieved the list of members")
  public List<Member> getAllMembers() {
    return memberService.getAllMembers();
  }

  /**
   * Retrieves detailed profile information of a member by their ID.
   *
   * @param id the unique identifier of the member
   * @return a ResponseEntity containing the member profile
   */
  @GetMapping("/{id}")
  @Operation(
      summary = "Get member by ID",
      description = "Retrieves detailed information of a member "
          + "by their database identifier."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Successfully retrieved the member details"),
      @ApiResponse(responseCode = "404",
          description = "Member not found with the specified ID")
  })
  public ResponseEntity<Member> getMemberById(
      @Parameter(description = "The unique identifier of the member",
          required = true)
      @PathVariable final String id) {
    return ResponseEntity.ok(memberService.getMemberById(id));
  }

  /**
   * Enables new users to self-register as members.
   *
   * @param member the member registration details
   * @return the registered member profile
   */
  @PostMapping("/signup")
  @Operation(
      summary = "Register a new member",
      description = "Enables a new user to self-register. Defaults "
          + "to MEMBER role."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "201",
          description = "Member successfully registered"),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid input data, or username/email "
              + "already exists"
      )
  })
  public ResponseEntity<Member> signup(
      @Valid @RequestBody final Member member) {
    return new ResponseEntity<>(memberService.registerMember(member),
        HttpStatus.CREATED);
  }

  /**
   * Updates general profile details (first name, last name, address).
   *
   * @param id the unique identifier of the member to update
   * @param member the updated member profile details
   * @return the updated member profile
   */
  @PutMapping("/{id}")
  @Operation(
      summary = "Update member details",
      description = "Updates a member's general profile information "
          + "(names, address)."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Member profile successfully updated"),
      @ApiResponse(responseCode = "400",
          description = "Invalid payload details"),
      @ApiResponse(responseCode = "404",
          description = "Member not found with the specified ID")
  })
  public ResponseEntity<Member> updateMember(
      @Parameter(description = "The unique identifier of the member "
          + "to update", required = true)
      @PathVariable final String id,
      @Valid @RequestBody final Member member) {
    return ResponseEntity.ok(memberService.updateMember(id, member));
  }

  /**
   * Allows setting a member's rhythmic dance expertise level (1-5).
   *
   * @param id the unique identifier of the member
   * @param level the level to set (1 to 5)
   * @return the updated member profile
   */
  @PatchMapping("/{id}/expertise")
  @PreAuthorize("hasRole('SECRETARY')")
  @Operation(
      summary = "Update expertise level",
      description = "Updates a member's expertise level (1-5). "
          + "Restricted to Secretary."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Expertise level successfully updated"),
      @ApiResponse(responseCode = "400",
          description = "Invalid level range"),
      @ApiResponse(responseCode = "404",
          description = "Member not found with the specified ID")
  })
  public ResponseEntity<Member> updateExpertise(
      @Parameter(description = "The unique identifier of the member",
          required = true)
      @PathVariable final String id,
      @Parameter(
          description = "Expertise level to set (1 to 5)",
          required = true)
      @RequestParam final int level) {
    return ResponseEntity.ok(
        memberService.updateExpertiseLevel(id, level));
  }

  /**
   * Enables a Secretary to update payment, medical certificate, and
   * validation status.
   *
   * @param id the unique identifier of the member
   * @param registrationStatusDto the status fields to update
   * @return the updated member profile
   */
  @PatchMapping("/{id}/registration-status")
  @PreAuthorize("hasRole('SECRETARY')")
  @Operation(
      summary = "Update registration status details",
      description = "Enables a Secretary to update payment, "
          + "certificate, and validation status."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Registration status successfully updated"),
      @ApiResponse(responseCode = "400",
          description = "Invalid status update payload"),
      @ApiResponse(responseCode = "404",
          description = "Member not found with the specified ID")
  })
  public ResponseEntity<Member> updateRegistrationStatus(
      @Parameter(description = "The unique identifier of the member",
          required = true)
      @PathVariable final String id,
      @Valid @RequestBody
      final RegistrationStatusDto registrationStatusDto) {
    return ResponseEntity.ok(
        memberService.updateRegistrationStatus(id,
            registrationStatusDto));
  }

  /**
   * Permanently deletes a member's record from the system.
   *
   * @param id the unique identifier of the member to delete
   * @return an empty ResponseEntity (204 No Content)
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('SECRETARY')")
  @Operation(
      summary = "Delete a member",
      description = "Permanently deletes a member's record from "
          + "the system."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "204",
          description = "Member successfully deleted"),
      @ApiResponse(responseCode = "404",
          description = "Member not found with the specified ID")
  })
  public ResponseEntity<Void> deleteMember(
      @Parameter(description = "The unique identifier of the member "
          + "to delete", required = true)
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
  @PreAuthorize("hasRole('SECRETARY')")
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

