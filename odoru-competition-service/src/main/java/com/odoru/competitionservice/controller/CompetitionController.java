package com.odoru.competitionservice.controller;

import java.util.List;
import com.odoru.competitionservice.model.Competition;
import com.odoru.competitionservice.model.CompetitionResult;
import com.odoru.competitionservice.service.CompetitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API endpoints for competition planning.
 */
@RestController
@RequestMapping("/api/competitions")
@RequiredArgsConstructor
@Tag(
    name = "Competitions",
    description = "APIs for competition planning and result recording."
)
public final class CompetitionController {

  private final CompetitionService competitionService;

  @PostMapping
  @PreAuthorize("hasRole('TEACHER')")
  @Operation(
      summary = "Schedule a new competition",
      description = "Enables an instructor to plan a competition. Ensures "
          + "date constraints and teacher qualifications are met."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "201",
          description = "Competition planned successfully"),
      @ApiResponse(responseCode = "400",
          description = "Invalid request or business rule violations")
  })
  public ResponseEntity<Competition> createCompetition(
      @Valid @RequestBody final Competition competition) {
    return new ResponseEntity<>(
        competitionService.createCompetition(competition),
        HttpStatus.CREATED);
  }

  @GetMapping
  @Operation(
      summary = "Get competitions",
      description = "Retrieves competitions with optional filters for target "
          + "level or teacher ID."
  )
  @ApiResponse(responseCode = "200",
      description = "Successfully retrieved the competitions")
  public ResponseEntity<List<Competition>> getCompetitions(
      @Parameter(description = "Filter by target level")
      @RequestParam(required = false) final Integer level,
      @Parameter(description = "Filter by teacher ID")
      @RequestParam(required = false) final String teacherId) {
    if (level != null) {
      return ResponseEntity.ok(
          competitionService.getCompetitionsByLevel(level));
    }
    if (teacherId != null) {
      return ResponseEntity.ok(
          competitionService.getCompetitionsByTeacher(teacherId));
    }
    return ResponseEntity.ok(competitionService.getAllCompetitions());
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Get competition by ID",
      description = "Retrieves detailed information of a competition by "
          + "its identifier."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Successfully retrieved the competition details"),
      @ApiResponse(responseCode = "404",
          description = "Competition not found with the specified ID")
  })
  public ResponseEntity<Competition> getCompetitionById(
      @Parameter(description = "The unique identifier of the competition",
          required = true)
      @PathVariable final String id) {
    return ResponseEntity.ok(competitionService.getCompetitionById(id));
  }

  @GetMapping("/student/{studentId}")
  @Operation(
      summary = "Get student competitions",
      description = "Retrieves the list of competitions the student is de "
          + "facto registered in based on their expertise level."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Successfully retrieved student competitions"),
      @ApiResponse(responseCode = "404",
          description = "Student not found")
  })
  public ResponseEntity<List<Competition>> getCompetitionsByStudent(
      @Parameter(description = "The unique identifier of the student",
          required = true)
      @PathVariable final String studentId) {
    return ResponseEntity.ok(
        competitionService.getCompetitionsForStudent(studentId));
  }

  @PostMapping("/{competitionId}/results/{studentId}")
  @PreAuthorize("hasRole('TEACHER')")
  @Operation(
      summary = "Record competition result",
      description = "Enables any instructor to input a score (0.0-10.0, "
          + "1/10th precision) for a student in a competition."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Result saved successfully"),
      @ApiResponse(responseCode = "400",
          description = "Invalid score, teacher, or student eligibility")
  })
  public ResponseEntity<CompetitionResult> addOrUpdateResult(
      @Parameter(description = "The unique identifier of the competition",
          required = true)
      @PathVariable final String competitionId,
      @Parameter(description = "The unique identifier of the student",
          required = true)
      @PathVariable final String studentId,
      @Parameter(description = "The score (0.0 to 10.0)", required = true)
      @RequestParam final Double score,
      @Parameter(description = "The unique identifier of the teacher",
          required = true)
      @RequestParam final String teacherId) {
    return ResponseEntity.ok(
        competitionService.addOrUpdateResult(
            competitionId, studentId, score, teacherId));
  }

  @GetMapping("/results/student/{studentId}")
  @Operation(
      summary = "Get student results",
      description = "Retrieves all recorded competition scores for a student."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Successfully retrieved student results"),
      @ApiResponse(responseCode = "404",
          description = "Student not found")
  })
  public ResponseEntity<List<CompetitionResult>> getStudentResults(
      @Parameter(description = "The unique identifier of the student",
          required = true)
      @PathVariable final String studentId) {
    return ResponseEntity.ok(
        competitionService.getStudentResults(studentId));
  }
}
