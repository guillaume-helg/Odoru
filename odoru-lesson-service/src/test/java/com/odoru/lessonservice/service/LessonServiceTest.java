package com.odoru.lessonservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import com.odoru.lessonservice.client.MemberClient;
import com.odoru.lessonservice.dto.MemberDto;
import com.odoru.lessonservice.model.Lesson;
import com.odoru.lessonservice.model.MemberRole;
import com.odoru.lessonservice.repository.LessonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LessonServiceTest {

  @Mock
  private LessonRepository lessonRepository;

  @Mock
  private MemberClient memberClient;

  @InjectMocks
  private LessonService lessonService;

  @Test
  public void testCreateLesson_Success() {
    final Lesson lesson = Lesson.builder()
        .title("Rythmic Intro")
        .targetLevel(2)
        .dateTime(LocalDateTime.now().plusDays(8))
        .duration(60)
        .teacherId("teacher-1")
        .location("Studio A")
        .build();

    final MemberDto teacher = MemberDto.builder()
        .id("teacher-1")
        .role(MemberRole.TEACHER)
        .expertiseLevel(3)
        .build();

    when(memberClient.getMemberById("teacher-1")).thenReturn(teacher);
    when(lessonRepository.save(any(Lesson.class))).thenReturn(lesson);

    final Lesson created = lessonService.createLesson(lesson);

    assertNotNull(created);
    assertEquals("Rythmic Intro", created.getTitle());
    verify(lessonRepository).save(lesson);
  }

  @Test
  public void testCreateLesson_InvalidDate() {
    final Lesson lesson = Lesson.builder()
        .title("Rythmic Intro")
        .targetLevel(2)
        .dateTime(LocalDateTime.now().plusDays(6))
        .duration(60)
        .teacherId("teacher-1")
        .location("Studio A")
        .build();

    assertThrows(IllegalArgumentException.class, () ->
        lessonService.createLesson(lesson));

    verify(memberClient, never()).getMemberById(any());
    verify(lessonRepository, never()).save(any());
  }

  @Test
  public void testCreateLesson_NotATeacher() {
    final Lesson lesson = Lesson.builder()
        .title("Rythmic Intro")
        .targetLevel(2)
        .dateTime(LocalDateTime.now().plusDays(8))
        .duration(60)
        .teacherId("student-1")
        .location("Studio A")
        .build();

    final MemberDto student = MemberDto.builder()
        .id("student-1")
        .role(MemberRole.MEMBER)
        .expertiseLevel(3)
        .build();

    when(memberClient.getMemberById("student-1")).thenReturn(student);

    assertThrows(IllegalArgumentException.class, () ->
        lessonService.createLesson(lesson));

    verify(lessonRepository, never()).save(any());
  }

  @Test
  public void testCreateLesson_ExpertiseLevelTooLow() {
    final Lesson lesson = Lesson.builder()
        .title("Rythmic Advanced")
        .targetLevel(4)
        .dateTime(LocalDateTime.now().plusDays(8))
        .duration(60)
        .teacherId("teacher-1")
        .location("Studio A")
        .build();

    final MemberDto teacher = MemberDto.builder()
        .id("teacher-1")
        .role(MemberRole.TEACHER)
        .expertiseLevel(3)
        .build();

    when(memberClient.getMemberById("teacher-1")).thenReturn(teacher);

    assertThrows(IllegalArgumentException.class, () ->
        lessonService.createLesson(lesson));

    verify(lessonRepository, never()).save(any());
  }

  @Test
  public void testGetLessonsForStudent_Success() {
    final MemberDto student = MemberDto.builder()
        .id("student-1")
        .role(MemberRole.MEMBER)
        .expertiseLevel(3)
        .build();

    final Lesson lesson = Lesson.builder()
        .title("Level 3 Lesson")
        .targetLevel(3)
        .build();

    when(memberClient.getMemberById("student-1")).thenReturn(student);
    when(lessonRepository.findByTargetLevel(3))
        .thenReturn(Collections.singletonList(lesson));

    final List<Lesson> lessons = lessonService
        .getLessonsForStudent("student-1");

    assertNotNull(lessons);
    assertEquals(1, lessons.size());
    assertEquals(3, lessons.get(0).getTargetLevel());
  }
}
