package com.odoru.lessonservice.repository;

import java.util.List;
import com.odoru.lessonservice.model.Lesson;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface managing MongoDB persistence for Lesson entities.
 */
@Repository
public interface LessonRepository extends MongoRepository<Lesson, String> {

  /**
   * Finds lessons by their target expertise level.
   *
   * @param targetLevel the target expertise level
   * @return the list of lessons matching the target level
   */
  List<Lesson> findByTargetLevel(int targetLevel);

  /**
   * Finds lessons taught by a specific teacher.
   *
   * @param teacherId the unique identifier of the teacher
   * @return the list of lessons taught by the teacher
   */
  List<Lesson> findByTeacherId(String teacherId);
}
