package com.odoru.badgeservice.repository;

import java.util.List;
import java.util.Optional;
import com.odoru.badgeservice.model.AttendanceLog;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * MongoDB repository for {@link AttendanceLog} entities.
 */
public interface AttendanceLogRepository
    extends MongoRepository<AttendanceLog, String> {

  List<AttendanceLog> findByMemberId(String memberId);

  List<AttendanceLog> findByLessonId(String lessonId);

  Optional<AttendanceLog> findByMemberIdAndLessonId(
      String memberId, String lessonId);
}
