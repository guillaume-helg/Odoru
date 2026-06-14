package com.odoru.badgeservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.odoru.badgeservice.client.LessonClient;
import com.odoru.badgeservice.client.MemberClient;
import com.odoru.badgeservice.dto.LessonDto;
import com.odoru.badgeservice.model.AttendanceLog;
import com.odoru.badgeservice.model.BadgeAssociation;
import com.odoru.badgeservice.repository.AttendanceLogRepository;
import com.odoru.badgeservice.repository.BadgeAssociationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class handling business logic for badge association and attendance.
 */
@Service
@RequiredArgsConstructor
public class BadgeService {

  private final BadgeAssociationRepository badgeAssociationRepository;
  private final AttendanceLogRepository attendanceLogRepository;
  private final MemberClient memberClient;
  private final LessonClient lessonClient;

  /**
   * Associates a badge with a member.
   *
   * @throws IllegalArgumentException if the badge number is already assigned to someone else
   */
  public BadgeAssociation associateBadge(
      final String memberId, final String badgeNumber) {
    // 1. Verify member exists in Member Service
    memberClient.verifyMemberExists(memberId);

    // 2. Verify badge is not in use by someone else
    final Optional<BadgeAssociation> existingBadge =
        badgeAssociationRepository.findByBadgeNumber(badgeNumber);
    if (existingBadge.isPresent()
        && !existingBadge.get().getMemberId().equals(memberId)) {
      throw new IllegalArgumentException(
          "Badge number is already assigned to another member");
    }

    // 3. Find existing association for member or create a new one
    final BadgeAssociation association = badgeAssociationRepository
        .findByMemberId(memberId)
        .orElseGet(() -> BadgeAssociation.builder()
            .memberId(memberId)
            .build());

    association.setBadgeNumber(badgeNumber);
    return badgeAssociationRepository.save(association);
  }

  /**
   * Dissociates a badge from its owner.
   *
   * @throws IllegalArgumentException if no badge association exists for the member
   */
  public void dissociateBadge(final String memberId) {
    final BadgeAssociation association = badgeAssociationRepository
        .findByMemberId(memberId)
        .orElseThrow(() -> new IllegalArgumentException(
            "No badge association found for member: " + memberId));
    badgeAssociationRepository.delete(association);
  }

  /**
   * Logs a student attendance via badge swiping at a lesson.
   *
   * @throws IllegalArgumentException if the badge number is unrecognized
   */
  public AttendanceLog logAttendance(
      final String badgeNumber, final String lessonId) {
    // 1. Resolve badge to member ID
    final BadgeAssociation association = badgeAssociationRepository
        .findByBadgeNumber(badgeNumber)
        .orElseThrow(() -> new IllegalArgumentException(
            "Unrecognized badge number: " + badgeNumber));

    final String memberId = association.getMemberId();

    // 2. Verify lesson exists in Lesson Service
    lessonClient.getLessonById(lessonId);

    // 3. Prevent duplicate scans
    final Optional<AttendanceLog> existingLog = attendanceLogRepository
        .findByMemberIdAndLessonId(memberId, lessonId);
    if (existingLog.isPresent()) {
      return existingLog.get();
    }

    // 4. Save scan
    final AttendanceLog log = AttendanceLog.builder()
        .memberId(memberId)
        .lessonId(lessonId)
        .timestamp(LocalDateTime.now())
        .build();

    return attendanceLogRepository.save(log);
  }

  public List<LessonDto> getStudentLessons(final String studentId) {
    // Verify student exists
    memberClient.verifyMemberExists(studentId);

    final List<AttendanceLog> logs = attendanceLogRepository
        .findByMemberId(studentId);

    final List<LessonDto> lessons = new ArrayList<>();
    for (final AttendanceLog log : logs) {
      try {
        lessons.add(lessonClient.getLessonById(log.getLessonId()));
      } catch (Exception ex) {
        // Skip or handle missing lessons (e.g. if a lesson was deleted)
      }
    }
    return lessons;
  }

  public List<String> getLessonAttendees(final String lessonId) {
    // 1. Verify lesson exists in Lesson Service
    lessonClient.getLessonById(lessonId);

    final List<AttendanceLog> logs = attendanceLogRepository
        .findByLessonId(lessonId);

    final List<String> studentIds = new ArrayList<>();
    for (final AttendanceLog log : logs) {
      studentIds.add(log.getMemberId());
    }
    return studentIds;
  }

  public List<BadgeAssociation> getAllAssociations() {
    return badgeAssociationRepository.findAll();
  }
}
