package com.odoru.badgeservice.controller;

import java.util.List;
import com.odoru.badgeservice.dto.LessonDto;
import com.odoru.badgeservice.model.AttendanceLog;
import com.odoru.badgeservice.model.BadgeAssociation;
import com.odoru.badgeservice.service.BadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class managing REST API endpoints for badge allocation.
 */
@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
@Tag(
    name = "Badges",
    description = "APIs for badge association and attendance swiping."
)
public class BadgeController {

  /** Badge service dependency. */
  private final BadgeService badgeService;

  /**
   * Associates a badge number with a member.
   *
   * @param memberId the member identifier
   * @param badgeNumber the unique random badge number
   * @return the saved association
   */
  @PostMapping("/associate")
  @PreAuthorize("hasRole('SECRETARY')")
  @Operation(
      summary = "Associate a badge to a member",
      description = "Links a unique badge number to a member ID. Restricted "
          + "to Secretary."
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

  /**
   * Dissociates any badge linked to a member.
   *
   * @param memberId the member identifier
   * @return an empty response (204 No Content)
   */
  @PostMapping("/dissociate/{memberId}")
  @PreAuthorize("hasRole('SECRETARY')")
  @Operation(
      summary = "Dissociate badge",
      description = "Unlinks the badge associated with the specified member. "
          + "Restricted to Secretary."
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

  /**
   * Simulates a badge swiping event at a course session.
   *
   * @param badgeNumber the swiped badge number
   * @param lessonId the lesson identifier
   * @return the saved attendance log
   */
  @PostMapping("/scan")
  @Operation(
      summary = "Simulate a badge scan",
      description = "Logs student presence when a badge is swiped at a reader."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "201",
          description = "Attendance logged successfully"),
      @ApiResponse(responseCode = "400",
          description = "Unrecognized badge or invalid lesson")
  })
  public ResponseEntity<AttendanceLog> scanBadge(
      @Parameter(description = "The swiped badge number", required = true)
      @RequestParam final String badgeNumber,
      @Parameter(description = "The unique identifier of the lesson",
          required = true)
      @RequestParam final String lessonId) {
    return new ResponseEntity<>(
        badgeService.logAttendance(badgeNumber, lessonId),
        HttpStatus.CREATED);
  }

  /**
   * Retrieves all lessons attended by a student.
   *
   * @param studentId the student identifier
   * @return the list of lessons attended
   */
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

  /**
   * Retrieves the list of student IDs present at a specific lesson.
   *
   * @param lessonId the lesson identifier
   * @return the list of student IDs present
   */
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
