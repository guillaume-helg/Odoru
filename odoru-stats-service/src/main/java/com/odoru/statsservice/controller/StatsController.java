package com.odoru.statsservice.controller;

import java.time.LocalDateTime;
import java.util.List;
import com.odoru.statsservice.dto.CourseSummaryDto;
import com.odoru.statsservice.dto.LessonAttendanceDto;
import com.odoru.statsservice.dto.StudentCompetitionResultDto;
import com.odoru.statsservice.dto.StudentCoursePresenceDto;
import com.odoru.statsservice.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class exposing statistics endpoints for the Club President.
 */
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@Tag(
    name = "Stats",
    description = "APIs for club statistics aggregation, restricted to President."
)
public class StatsController {

  /** Stats service dependency. */
  private final StatsService statsService;

  /**
   * Retrieves overall courses metrics (total count, average student attendance).
   *
   * @return the course summary stats
   */
  @GetMapping("/courses/summary")
  @Operation(
      summary = "Get overall course stats",
      description = "Retrieves total scheduled courses and the average "
          + "attendance count per course."
  )
  @ApiResponse(responseCode = "200",
      description = "Successfully retrieved course summary statistics")
  public ResponseEntity<CourseSummaryDto> getCourseSummary() {
    return ResponseEntity.ok(statsService.getCourseSummary());
  }

  /**
   * Retrieves detail of students present at a given course.
   *
   * @param lessonId the unique identifier of the lesson
   * @return the list and count of attendees
   */
  @GetMapping("/courses/{lessonId}/attendance")
  @Operation(
      summary = "Get attendees for a course",
      description = "Retrieves the count and list of students present at "
          + "a specific course slot."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Successfully retrieved attendee details"),
      @ApiResponse(responseCode = "404",
          description = "Course not found")
  })
  public ResponseEntity<LessonAttendanceDto> getLessonAttendance(
      @Parameter(description = "The unique identifier of the course/lesson",
          required = true)
      @PathVariable final String lessonId) {
    return ResponseEntity.ok(statsService.getLessonAttendance(lessonId));
  }

  /**
   * Compiles the list of courses for a student with present/absent logs.
   *
   * @param studentId the student identifier
   * @param start optional start date range filter
   * @param end optional end date range filter
   * @return the list of course presence logs
   */
  @GetMapping("/students/{studentId}/attendance")
  @Operation(
      summary = "Get student course presence/absences",
      description = "Compiles the list of target level courses for a student, "
          + "marking their attendance as present or absent, with optional "
          + "date range filters."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Successfully retrieved student course log"),
      @ApiResponse(responseCode = "404",
          description = "Student not found")
  })
  public ResponseEntity<List<StudentCoursePresenceDto>> getStudentPresence(
      @Parameter(description = "The unique identifier of the student",
          required = true)
      @PathVariable final String studentId,
      @Parameter(description = "Filter start date/time (ISO format)")
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      final LocalDateTime start,
      @Parameter(description = "Filter end date/time (ISO format)")
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      final LocalDateTime end) {
    return ResponseEntity.ok(
        statsService.getStudentCoursePresence(studentId, start, end));
  }

  /**
   * Retrieves the count of competitions scheduled for a target expertise level.
   *
   * @param level the target expertise level
   * @return the count of competitions
   */
  @GetMapping("/competitions/summary")
  @Operation(
      summary = "Get competition count by level",
      description = "Counts the total scheduled competitions matching a "
          + "target expertise level."
  )
  @ApiResponse(responseCode = "200",
      description = "Successfully retrieved competition count")
  public ResponseEntity<Long> getCompetitionsCount(
      @Parameter(description = "Target expertise level", required = true)
      @RequestParam final int level) {
    return ResponseEntity.ok(statsService.getCompetitionsCountByLevel(level));
  }

  /**
   * Compiles student's competitions with their achieved results/scores.
   *
   * @param studentId the student identifier
   * @param start optional start date range filter
   * @param end optional end date range filter
   * @return the list of competitions with results
   */
  @GetMapping("/students/{studentId}/competitions")
  @Operation(
      summary = "Get student competition results",
      description = "Retrieves student's competitions with their achieved "
          + "results, with optional date range filters."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Successfully retrieved student competition results"),
      @ApiResponse(responseCode = "404",
          description = "Student not found")
  })
  public ResponseEntity<List<StudentCompetitionResultDto>>
      getStudentCompetitions(
          @Parameter(description = "The unique identifier of the student",
              required = true)
          @PathVariable final String studentId,
          @Parameter(description = "Filter start date/time (ISO format)")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          final LocalDateTime start,
          @Parameter(description = "Filter end date/time (ISO format)")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          final LocalDateTime end) {
    return ResponseEntity.ok(
        statsService.getStudentCompetitionResults(studentId, start, end));
  }
}
