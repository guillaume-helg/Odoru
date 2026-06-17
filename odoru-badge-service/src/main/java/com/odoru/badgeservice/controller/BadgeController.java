package com.odoru.badgeservice.controller;

import com.odoru.badgeservice.config.RabbitMQConfig;
import com.odoru.badgeservice.dto.AttendanceScanRequest;
import com.odoru.badgeservice.dto.LessonDto;
import com.odoru.badgeservice.model.BadgeAssociation;
import com.odoru.badgeservice.service.BadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
@Tag(
    name = "Badges",
    description = "APIs for badge association and attendance swiping."
)
public class BadgeController {

  private final BadgeService badgeService;
  private final RabbitTemplate rabbitTemplate;

  @PostMapping("/associate")
  @PreAuthorize("hasAnyRole('SECRETARY', 'PRESIDENT')")
  @Operation(
      summary = "Associate a badge to a member",
      description = "Links a unique badge number to a member ID. Restricted "
          + "to Secretary or President."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Badge successfully associated"),
      @ApiResponse(responseCode = "400",
          description = "Invalid input or badge already in use")
  })
  public ResponseEntity<BadgeAssociation> associateBadge(
      @Parameter(description = "The unique identifier of the member",
          required = true)
      @RequestParam final String memberId,
      @Parameter(description = "The unique random badge number",
          required = true)
      @RequestParam final String badgeNumber) {
    return ResponseEntity.ok(
        badgeService.associateBadge(memberId, badgeNumber));
  }

  @PostMapping("/dissociate/{memberId}")
  @PreAuthorize("hasAnyRole('SECRETARY', 'PRESIDENT')")
  @Operation(
      summary = "Dissociate badge",
      description = "Unlinks the badge associated with the specified member. "
          + "Restricted to Secretary or President."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "204",
          description = "Badge successfully dissociated"),
      @ApiResponse(responseCode = "404",
          description = "No badge association found for the member")
  })
  public ResponseEntity<Void> dissociateBadge(
      @Parameter(description = "The unique identifier of the member",
          required = true)
      @PathVariable final String memberId) {
    badgeService.dissociateBadge(memberId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('SECRETARY', 'PRESIDENT')")
  @Operation(
      summary = "Get all badge associations",
      description = "Retrieves the complete list of member-to-badge mappings."
  )
  public ResponseEntity<List<BadgeAssociation>> getAllAssociations() {
    return ResponseEntity.ok(badgeService.getAllAssociations());
  }

  @PostMapping("/scan")
  @Operation(
      summary = "Simulate a badge scan",
      description = "Queues a student presence scan event for asynchronous "
          + "validation and processing."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "202",
          description = "Badge scan received and queued successfully")
  })
  public ResponseEntity<Void> scanBadge(
      @Parameter(description = "The swiped badge number", required = true)
      @RequestParam final String badgeNumber,
      @Parameter(description = "The unique identifier of the lesson",
          required = true)
      @RequestParam final String lessonId) {
    final AttendanceScanRequest scanRequest = AttendanceScanRequest.builder()
        .badgeNumber(badgeNumber)
        .lessonId(lessonId)
        .build();
    rabbitTemplate.convertAndSend(
        RabbitMQConfig.ATTENDANCE_EXCHANGE,
        RabbitMQConfig.ATTENDANCE_ROUTING_KEY,
        scanRequest);
    return ResponseEntity.accepted().build();
  }

  @GetMapping("/attendance/student/{studentId}")
  @Operation(
      summary = "Get attended lessons",
      description = "Retrieves a list of all course slots the student has "
          + "successfully attended."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Successfully retrieved attended lessons list"),
      @ApiResponse(responseCode = "404",
          description = "Student not found")
  })
  public ResponseEntity<List<LessonDto>> getStudentLessons(
      @Parameter(description = "The unique identifier of the student",
          required = true)
      @PathVariable final String studentId) {
    return ResponseEntity.ok(badgeService.getStudentLessons(studentId));
  }

  @GetMapping("/attendance/lesson/{lessonId}")
  @Operation(
      summary = "Get lesson attendees",
      description = "Retrieves the list of member IDs who swiped in for a "
          + "specific lesson."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Successfully retrieved attendee list"),
      @ApiResponse(responseCode = "404",
          description = "Lesson not found")
  })
  public ResponseEntity<List<String>> getLessonAttendees(
      @Parameter(description = "The unique identifier of the lesson",
          required = true)
      @PathVariable final String lessonId) {
    return ResponseEntity.ok(badgeService.getLessonAttendees(lessonId));
  }
}
