package com.odoru.badgeservice.repository;

import java.util.List;
import java.util.Optional;
import com.odoru.badgeservice.model.AttendanceLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface managing MongoDB persistence for AttendanceLog entities.
 */
@Repository
public interface AttendanceLogRepository
    extends MongoRepository<AttendanceLog, String> {

  /**
   * Finds attendance logs for a specific member.
   *
   * @param memberId the unique identifier of the member
   * @return the list of attendance logs
   */
  List<AttendanceLog> findByMemberId(String memberId);

  /**
   * Finds attendance logs for a specific lesson/course slot.
   *
   * @param lessonId the unique identifier of the lesson
   * @return the list of attendance logs
   */
  List<AttendanceLog> findByLessonId(String lessonId);

  /**
   * Finds a specific attendance log for a member at a given lesson.
   *
   * @param memberId the unique identifier of the member
   * @param lessonId the unique identifier of the lesson
   * @return an Optional containing the log if found
   */
  Optional<AttendanceLog> findByMemberIdAndLessonId(
      String memberId, String lessonId);
}
