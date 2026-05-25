package com.odoru.competitionservice.repository;

import java.util.List;
import java.util.Optional;
import com.odoru.competitionservice.model.CompetitionResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface managing MongoDB persistence for CompetitionResult entities.
 */
@Repository
public interface CompetitionResultRepository
    extends MongoRepository<CompetitionResult, String> {

  /**
   * Finds competition results for a specific student.
   *
   * @param studentId the unique identifier of the student
   * @return the list of results
   */
  List<CompetitionResult> findByStudentId(String studentId);

  /**
   * Finds competition results for a specific competition.
   *
   * @param competitionId the unique identifier of the competition
   * @return the list of results
   */
  List<CompetitionResult> findByCompetitionId(String competitionId);

  /**
   * Finds a student's score for a specific competition.
   *
   * @param competitionId the unique identifier of the competition
   * @param studentId the unique identifier of the student
   * @return an Optional containing the result if found
   */
  Optional<CompetitionResult> findByCompetitionIdAndStudentId(
      String competitionId, String studentId);
}
