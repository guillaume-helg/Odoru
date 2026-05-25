package com.odoru.competitionservice.repository;

import java.util.List;
import com.odoru.competitionservice.model.Competition;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface managing MongoDB persistence for Competition entities.
 */
@Repository
public interface CompetitionRepository
    extends MongoRepository<Competition, String> {

  /**
   * Finds competitions by target level.
   *
   * @param targetLevel the target expertise level
   * @return the list of matching competitions
   */
  List<Competition> findByTargetLevel(int targetLevel);

  /**
   * Finds competitions organized by a specific teacher.
   *
   * @param teacherId the unique identifier of the teacher
   * @return the list of matching competitions
   */
  List<Competition> findByTeacherId(String teacherId);
}
