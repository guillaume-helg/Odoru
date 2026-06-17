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

@Service
@RequiredArgsConstructor
public class LessonService {

  private static final int MIN_DAYS_IN_ADVANCE = 7;

  private final LessonRepository lessonRepository;
  private final MemberClient memberClient;

  public Lesson createLesson(final Lesson lesson) {

    final LocalDate minAllowedDate = LocalDate.now()
        .plusDays(MIN_DAYS_IN_ADVANCE);
    if (lesson.getDateTime().toLocalDate().isBefore(minAllowedDate)) {
      throw new IllegalArgumentException(
          "Lesson date must be at least 7 days in the future");
    }

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

  public List<Lesson> getAllLessons() {
    return lessonRepository.findAll();
  }

  public List<Lesson> getLessonsByLevel(final int level) {
    return lessonRepository.findByTargetLevel(level);
  }

  public List<Lesson> getLessonsByTeacher(final String teacherId) {
    return lessonRepository.findByTeacherId(teacherId);
  }

  public List<Lesson> getLessonsForStudent(final String studentId) {
    final MemberDto student = memberClient.getMemberById(studentId);
    return getLessonsByLevel(student.getExpertiseLevel());
  }

  public Lesson getLessonById(final String id) {
    return lessonRepository.findById(id)
        .orElseThrow(() -> new RuntimeException(
            "Lesson not found with id: " + id));
  }
}
