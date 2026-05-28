package com.odoru.lessonservice.repository;

import java.util.List;
import com.odoru.lessonservice.model.Lesson;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * MongoDB repository for {@link Lesson} entities.
 */
public interface LessonRepository extends MongoRepository<Lesson, String> {

  List<Lesson> findByTargetLevel(int targetLevel);

  List<Lesson> findByTeacherId(String teacherId);
}
