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

@Service
@RequiredArgsConstructor
public class BadgeService {

  private final BadgeAssociationRepository badgeAssociationRepository;
  private final AttendanceLogRepository attendanceLogRepository;
  private final MemberClient memberClient;
  private final LessonClient lessonClient;

  public BadgeAssociation associateBadge(
      final String memberId, final String badgeNumber) {

    memberClient.verifyMemberExists(memberId);

    final Optional<BadgeAssociation> existingBadge =
        badgeAssociationRepository.findByBadgeNumber(badgeNumber);
    if (existingBadge.isPresent()
        && !existingBadge.get().getMemberId().equals(memberId)) {
      throw new IllegalArgumentException(
          "Badge number is already assigned to another member");
    }

    final BadgeAssociation association = badgeAssociationRepository
        .findByMemberId(memberId)
        .orElseGet(() -> BadgeAssociation.builder()
            .memberId(memberId)
            .build());

    association.setBadgeNumber(badgeNumber);
    return badgeAssociationRepository.save(association);
  }

  public void dissociateBadge(final String memberId) {
    final BadgeAssociation association = badgeAssociationRepository
        .findByMemberId(memberId)
        .orElseThrow(() -> new IllegalArgumentException(
            "No badge association found for member: " + memberId));
    badgeAssociationRepository.delete(association);
  }

  public AttendanceLog logAttendance(
      final String badgeNumber, final String lessonId) {

    final BadgeAssociation association = badgeAssociationRepository
        .findByBadgeNumber(badgeNumber)
        .orElseThrow(() -> new IllegalArgumentException(
            "Unrecognized badge number: " + badgeNumber));

    final String memberId = association.getMemberId();

    lessonClient.getLessonById(lessonId);

    final Optional<AttendanceLog> existingLog = attendanceLogRepository
        .findByMemberIdAndLessonId(memberId, lessonId);
    if (existingLog.isPresent()) {
      return existingLog.get();
    }

    final AttendanceLog log = AttendanceLog.builder()
        .memberId(memberId)
        .lessonId(lessonId)
        .timestamp(LocalDateTime.now())
        .build();

    return attendanceLogRepository.save(log);
  }

  public List<LessonDto> getStudentLessons(final String studentId) {

    memberClient.verifyMemberExists(studentId);

    final List<AttendanceLog> logs = attendanceLogRepository
        .findByMemberId(studentId);

    final List<LessonDto> lessons = new ArrayList<>();
    for (final AttendanceLog log : logs) {
      try {
        lessons.add(lessonClient.getLessonById(log.getLessonId()));
      } catch (Exception ex) {

      }
    }
    return lessons;
  }

  public List<String> getLessonAttendees(final String lessonId) {

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
