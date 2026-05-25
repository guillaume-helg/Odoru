package com.odoru.lessonservice.service;

import java.time.LocalDate;
import java.util.List;
import com.odoru.lessonservice.client.MemberClient;
import com.odoru.lessonservice.dto.MemberDto;
import com.odoru.lessonservice.model.Lesson;
import com.odoru.lessonservice.model.MemberRole;
import com.odoru.lessonservice.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class handling business logic for lesson planning and slot lookup.
 */
@Service
@RequiredArgsConstructor
public class LessonService {

  /** Minimum days in advance to schedule a lesson. */
  private static final int MIN_DAYS_IN_ADVANCE = 7;

  /** Lesson repository. */
  private final LessonRepository lessonRepository;

  /** Member client. */
  private final MemberClient memberClient;

  /**
   * Creates a new lesson after validating all business rules.
   *
   * @param lesson the lesson to create
   * @return the saved lesson
   */
  public Lesson createLesson(final Lesson lesson) {
    // 1. Date check: must be >= 7 days in advance
    final LocalDate minAllowedDate = LocalDate.now()
        .plusDays(MIN_DAYS_IN_ADVANCE);
    if (lesson.getDateTime().toLocalDate().isBefore(minAllowedDate)) {
      throw new IllegalArgumentException(
          "Lesson date must be at least 7 days in the future");
    }

    // 2. Fetch and validate teacher
    final MemberDto teacher = memberClient.getMemberById(
        lesson.getTeacherId());

    if (teacher.getRole() != MemberRole.TEACHER) {
      throw new IllegalArgumentException(
          "Assigned member is not a teacher");
    }

    if (teacher.getExpertiseLevel() < lesson.getTargetLevel()) {
      throw new IllegalArgumentException(
          "Teacher is not qualified to teach at level "
              + lesson.getTargetLevel());
    }

    return lessonRepository.save(lesson);
  }

  /**
   * Retrieves all lessons.
   *
   * @return all lessons
   */
  public List<Lesson> getAllLessons() {
    return lessonRepository.findAll();
  }

  /**
   * Retrieves all lessons matching a specific target level.
   *
   * @param level the target level
   * @return list of matching lessons
   */
  public List<Lesson> getLessonsByLevel(final int level) {
    return lessonRepository.findByTargetLevel(level);
  }

  /**
   * Retrieves all lessons taught by a specific teacher.
   *
   * @param teacherId the unique identifier of the teacher
   * @return list of lessons taught by the teacher
   */
  public List<Lesson> getLessonsByTeacher(final String teacherId) {
    return lessonRepository.findByTeacherId(teacherId);
  }

  /**
   * Retrieves all lessons a student is enrolled in de facto.
   * "Les élèves d’un niveau X sont de facto inscrits à tous les cours de
   * niveau X."
   *
   * @param studentId the unique identifier of the student
   * @return list of lessons for the student's level
   */
  public List<Lesson> getLessonsForStudent(final String studentId) {
    final MemberDto student = memberClient.getMemberById(studentId);
    return getLessonsByLevel(student.getExpertiseLevel());
  }

  /**
   * Finds a lesson by its ID. Throws an exception if the lesson is not found.
   *
   * @param id the unique identifier of the lesson
   * @return the lesson entity
   * @throws RuntimeException if the lesson is not found
   */
  public Lesson getLessonById(final String id) {
    return lessonRepository.findById(id)
        .orElseThrow(() -> new RuntimeException(
            "Lesson not found with id: " + id));
  }
}
