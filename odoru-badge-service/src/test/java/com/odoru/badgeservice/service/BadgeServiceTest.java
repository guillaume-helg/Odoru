package com.odoru.badgeservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import com.odoru.badgeservice.client.LessonClient;
import com.odoru.badgeservice.client.MemberClient;
import com.odoru.badgeservice.dto.LessonDto;
import com.odoru.badgeservice.model.AttendanceLog;
import com.odoru.badgeservice.model.BadgeAssociation;
import com.odoru.badgeservice.repository.AttendanceLogRepository;
import com.odoru.badgeservice.repository.BadgeAssociationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the BadgeService class.
 */
@ExtendWith(MockitoExtension.class)
public class BadgeServiceTest {

  @Mock
  private BadgeAssociationRepository badgeAssociationRepository;

  @Mock
  private AttendanceLogRepository attendanceLogRepository;

  @Mock
  private MemberClient memberClient;

  @Mock
  private LessonClient lessonClient;

  @InjectMocks
  private BadgeService badgeService;

  /**
   * Tests associating a badge successfully.
   */
  @Test
  public void testAssociateBadge_Success() {
    final String memberId = "member-123";
    final String badgeNumber = "badge-456";

    final BadgeAssociation association = BadgeAssociation.builder()
        .memberId(memberId)
        .badgeNumber(badgeNumber)
        .build();

    doNothing().when(memberClient).verifyMemberExists(memberId);
    when(badgeAssociationRepository.findByBadgeNumber(badgeNumber))
        .thenReturn(Optional.empty());
    when(badgeAssociationRepository.findByMemberId(memberId))
        .thenReturn(Optional.empty());
    when(badgeAssociationRepository.save(any(BadgeAssociation.class)))
        .thenReturn(association);

    final BadgeAssociation saved = badgeService.associateBadge(
        memberId, badgeNumber);

    assertNotNull(saved);
    assertEquals(badgeNumber, saved.getBadgeNumber());
    verify(badgeAssociationRepository).save(any(BadgeAssociation.class));
  }

  /**
   * Tests that associating a badge already assigned to someone else fails.
   */
  @Test
  public void testAssociateBadge_AlreadyAssignedToOther() {
    final String memberId = "member-123";
    final String badgeNumber = "badge-456";

    final BadgeAssociation other = BadgeAssociation.builder()
        .memberId("other-member")
        .badgeNumber(badgeNumber)
        .build();

    doNothing().when(memberClient).verifyMemberExists(memberId);
    when(badgeAssociationRepository.findByBadgeNumber(badgeNumber))
        .thenReturn(Optional.of(other));

    assertThrows(IllegalArgumentException.class, () ->
        badgeService.associateBadge(memberId, badgeNumber));

    verify(badgeAssociationRepository, never()).save(any());
  }

  /**
   * Tests dissociating a badge successfully.
   */
  @Test
  public void testDissociateBadge_Success() {
    final String memberId = "member-123";
    final BadgeAssociation association = BadgeAssociation.builder()
        .memberId(memberId)
        .badgeNumber("badge-456")
        .build();

    when(badgeAssociationRepository.findByMemberId(memberId))
        .thenReturn(Optional.of(association));
    doNothing().when(badgeAssociationRepository).delete(association);

    badgeService.dissociateBadge(memberId);

    verify(badgeAssociationRepository).delete(association);
  }

  /**
   * Tests logging attendance (swiping badge) successfully.
   */
  @Test
  public void testLogAttendance_Success() {
    final String badgeNumber = "badge-456";
    final String lessonId = "lesson-789";
    final String memberId = "member-123";

    final BadgeAssociation association = BadgeAssociation.builder()
        .memberId(memberId)
        .badgeNumber(badgeNumber)
        .build();

    final LessonDto lesson = LessonDto.builder()
        .id(lessonId)
        .title("Introduction to Ballet")
        .build();

    final AttendanceLog log = AttendanceLog.builder()
        .memberId(memberId)
        .lessonId(lessonId)
        .timestamp(LocalDateTime.now())
        .build();

    when(badgeAssociationRepository.findByBadgeNumber(badgeNumber))
        .thenReturn(Optional.of(association));
    when(lessonClient.getLessonById(lessonId)).thenReturn(lesson);
    when(attendanceLogRepository.findByMemberIdAndLessonId(memberId, lessonId))
        .thenReturn(Optional.empty());
    when(attendanceLogRepository.save(any(AttendanceLog.class)))
        .thenReturn(log);

    final AttendanceLog savedLog = badgeService.logAttendance(
        badgeNumber, lessonId);

    assertNotNull(savedLog);
    assertEquals(memberId, savedLog.getMemberId());
    assertEquals(lessonId, savedLog.getLessonId());
    verify(attendanceLogRepository).save(any(AttendanceLog.class));
  }

  /**
   * Tests swiping with an unrecognized badge fails.
   */
  @Test
  public void testLogAttendance_UnrecognizedBadge() {
    final String badgeNumber = "unknown-badge";
    final String lessonId = "lesson-789";

    when(badgeAssociationRepository.findByBadgeNumber(badgeNumber))
        .thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () ->
        badgeService.logAttendance(badgeNumber, lessonId));

    verify(attendanceLogRepository, never()).save(any());
  }

  /**
   * Tests swiping twice for the same lesson is idempotent.
   */
  @Test
  public void testLogAttendance_DuplicateScan() {
    final String badgeNumber = "badge-456";
    final String lessonId = "lesson-789";
    final String memberId = "member-123";

    final BadgeAssociation association = BadgeAssociation.builder()
        .memberId(memberId)
        .badgeNumber(badgeNumber)
        .build();

    final LessonDto lesson = LessonDto.builder()
        .id(lessonId)
        .build();

    final AttendanceLog existingLog = AttendanceLog.builder()
        .memberId(memberId)
        .lessonId(lessonId)
        .timestamp(LocalDateTime.now())
        .build();

    when(badgeAssociationRepository.findByBadgeNumber(badgeNumber))
        .thenReturn(Optional.of(association));
    when(lessonClient.getLessonById(lessonId)).thenReturn(lesson);
    when(attendanceLogRepository.findByMemberIdAndLessonId(memberId, lessonId))
        .thenReturn(Optional.of(existingLog));

    final AttendanceLog result = badgeService.logAttendance(
        badgeNumber, lessonId);

    assertNotNull(result);
    assertEquals(existingLog, result);
    verify(attendanceLogRepository, never()).save(any());
  }

  /**
   * Tests retrieving the list of attended lessons for a student.
   */
  @Test
  public void testGetStudentLessons_Success() {
    final String studentId = "student-123";
    final String lessonId = "lesson-789";

    final AttendanceLog log = AttendanceLog.builder()
        .memberId(studentId)
        .lessonId(lessonId)
        .build();

    final LessonDto lesson = LessonDto.builder()
        .id(lessonId)
        .title("Salsa Basics")
        .build();

    doNothing().when(memberClient).verifyMemberExists(studentId);
    when(attendanceLogRepository.findByMemberId(studentId))
        .thenReturn(Collections.singletonList(log));
    when(lessonClient.getLessonById(lessonId)).thenReturn(lesson);

    final List<LessonDto> lessons = badgeService
        .getStudentLessons(studentId);

    assertNotNull(lessons);
    assertEquals(1, lessons.size());
    assertEquals("Salsa Basics", lessons.get(0).getTitle());
  }
}
