package com.odoru.competitionservice.repository;

import java.util.List;
import com.odoru.competitionservice.model.Competition;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface managing MongoDB persistence for Competition entities.
 */
public interface CompetitionRepository
    extends MongoRepository<Competition, String> {

  List<Competition> findByTargetLevel(int targetLevel);

  List<Competition> findByTeacherId(String teacherId);
}
