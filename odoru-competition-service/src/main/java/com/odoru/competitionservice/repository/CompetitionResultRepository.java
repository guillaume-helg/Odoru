package com.odoru.competitionservice.repository;

import java.util.List;
import java.util.Optional;
import com.odoru.competitionservice.model.CompetitionResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for CompetitionResult entities.
 */
public interface CompetitionResultRepository
    extends MongoRepository<CompetitionResult, String> {

  List<CompetitionResult> findByStudentId(String studentId);

  List<CompetitionResult> findByCompetitionId(String competitionId);

  Optional<CompetitionResult> findByCompetitionIdAndStudentId(
      String competitionId, String studentId);
}
