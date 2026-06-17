package com.odoru.badgeservice.repository;

import com.odoru.badgeservice.model.AttendanceLog;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AttendanceLogRepository
    extends MongoRepository<AttendanceLog, String>
{
    List<AttendanceLog> findByMemberId(String memberId);

    List<AttendanceLog> findByLessonId(String lessonId);

    Optional<AttendanceLog> findByMemberIdAndLessonId(
        String memberId,
        String lessonId
    );
}
