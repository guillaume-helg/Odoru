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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "APIs for managing rhythmic dance club members, including self-registration and administrative controls (Secretary roles)")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    @Operation(summary = "Get all members", description = "Retrieves a list of all registered members in the system.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of members")
    public List<Member> getAllMembers() {
        return memberService.getAllMembers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get member by ID", description = "Retrieves detailed information of a member by their unique database identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the member details"),
            @ApiResponse(responseCode = "404", description = "Member not found with the specified ID")
    })
    public ResponseEntity<Member> getMemberById(
            @Parameter(description = "The unique identifier of the member", required = true)
            @PathVariable String id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    @PostMapping("/signup")
    @Operation(summary = "Register a new member", description = "Enables a new user to self-register as a member. Defaults to MEMBER role with unvalidated registration status.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Member successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid input data, or username/email already exists")
    })
    public ResponseEntity<Member> signup(@Valid @RequestBody Member member) {
        return new ResponseEntity<>(memberService.registerMember(member), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update member details", description = "Updates a member's general profile information (first name, last name, address).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Member profile successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid payload details"),
            @ApiResponse(responseCode = "404", description = "Member not found with the specified ID")
    })
    public ResponseEntity<Member> updateMember(
            @Parameter(description = "The unique identifier of the member to update", required = true)
            @PathVariable String id,
            @Valid @RequestBody Member member) {
        return ResponseEntity.ok(memberService.updateMember(id, member));
    }

    @PatchMapping("/{id}/expertise")
    @Operation(summary = "Update expertise level", description = "Allows updating a member's rhythmic dance expertise level (1-5). Typically restricted to Secretary/Admin.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Expertise level successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid level range (must be between 1 and 5)"),
            @ApiResponse(responseCode = "404", description = "Member not found with the specified ID")
    })
    public ResponseEntity<Member> updateExpertise(
            @Parameter(description = "The unique identifier of the member", required = true)
            @PathVariable String id,
            @Parameter(description = "Expertise level to set (1 to 5)", required = true)
            @RequestParam int level) {
        return ResponseEntity.ok(memberService.updateExpertiseLevel(id, level));
    }

    @PatchMapping("/{id}/registration-status")
    @Operation(summary = "Update registration status details", description = "Enables a Secretary to update payment status, medical certificate submission, and validation status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registration status successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid status update payload"),
            @ApiResponse(responseCode = "404", description = "Member not found with the specified ID")
    })
    public ResponseEntity<Member> updateRegistrationStatus(
            @Parameter(description = "The unique identifier of the member", required = true)
            @PathVariable String id,
            @Valid @RequestBody RegistrationStatusDto registrationStatusDto) {
        return ResponseEntity.ok(memberService.updateRegistrationStatus(id, registrationStatusDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a member", description = "Permanently deletes a member's record from the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Member successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Member not found with the specified ID")
    })
    public ResponseEntity<Void> deleteMember(
            @Parameter(description = "The unique identifier of the member to delete", required = true)
            @PathVariable String id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
