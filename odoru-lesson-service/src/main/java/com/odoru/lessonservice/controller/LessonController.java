package com.odoru.lessonservice.controller;

import java.util.List;
import com.odoru.lessonservice.model.Lesson;
import com.odoru.lessonservice.service.LessonService;
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
 * Controller class managing REST API endpoints for course/lesson slots planning.
 */
@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
@Tag(
    name = "Lessons",
    description = "APIs for lesson planning and schedule queries."
)
public class LessonController {

  private final LessonService lessonService;

  @PostMapping
  @PreAuthorize("hasAnyRole('TEACHER', 'PRESIDENT')")
  @Operation(
      summary = "Schedule a new lesson",
      description = "Enables an instructor to plan a lesson. Ensures "
          + "date constraints and teacher certifications are met."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "201",
          description = "Lesson planned successfully"),
      @ApiResponse(responseCode = "400",
          description = "Invalid request or business rule violations")
  })
  public ResponseEntity<Lesson> createLesson(
      @Valid @RequestBody final Lesson lesson) {
    return new ResponseEntity<>(
        lessonService.createLesson(lesson), HttpStatus.CREATED);
  }

  @GetMapping
  @Operation(
      summary = "Get lessons",
      description = "Retrieves lessons with optional filters for target "
          + "level or teacher ID."
  )
  @ApiResponse(responseCode = "200",
      description = "Successfully retrieved the lessons")
  public ResponseEntity<List<Lesson>> getLessons(
      @Parameter(description = "Filter by target level")
      @RequestParam(required = false) final Integer level,
      @Parameter(description = "Filter by teacher ID")
      @RequestParam(required = false) final String teacherId) {
    if (level != null) {
      return ResponseEntity.ok(lessonService.getLessonsByLevel(level));
    }
    if (teacherId != null) {
      return ResponseEntity.ok(lessonService.getLessonsByTeacher(teacherId));
    }
    return ResponseEntity.ok(lessonService.getAllLessons());
  }

  @GetMapping("/teacher/{teacherId}")
  @Operation(
      summary = "Get lessons taught by a teacher",
      description = "Retrieves the list of scheduled lessons taught "
          + "by the specified teacher."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Successfully retrieved teacher schedule"),
      @ApiResponse(responseCode = "404",
          description = "Teacher not found")
  })
  public ResponseEntity<List<Lesson>> getLessonsByTeacher(
      @Parameter(description = "The unique identifier of the teacher",
          required = true)
      @PathVariable final String teacherId) {
    return ResponseEntity.ok(lessonService.getLessonsByTeacher(teacherId));
  }

  @GetMapping("/student/{studentId}")
  @Operation(
      summary = "Get lessons for a student",
      description = "Retrieves the list of lessons the student is de facto "
          + "registered in based on their expertise level."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Successfully retrieved student lessons"),
      @ApiResponse(responseCode = "404",
          description = "Student not found")
  })
  public ResponseEntity<List<Lesson>> getLessonsByStudent(
      @Parameter(description = "The unique identifier of the student",
          required = true)
      @PathVariable final String studentId) {
    return ResponseEntity.ok(lessonService.getLessonsForStudent(studentId));
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Get lesson by ID",
      description = "Retrieves detailed information of a lesson by "
          + "its identifier."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Successfully retrieved the lesson details"),
      @ApiResponse(responseCode = "404",
          description = "Lesson not found with the specified ID")
  })
  public ResponseEntity<Lesson> getLessonById(
      @Parameter(description = "The unique identifier of the lesson",
          required = true)
      @PathVariable final String id) {
    return ResponseEntity.ok(lessonService.getLessonById(id));
  }
}
