package com.odoru.competitionservice.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import com.odoru.competitionservice.client.MemberClient;
import com.odoru.competitionservice.dto.MemberDto;
import com.odoru.competitionservice.model.Competition;
import com.odoru.competitionservice.model.CompetitionResult;
import com.odoru.competitionservice.model.MemberRole;
import com.odoru.competitionservice.repository.CompetitionRepository;
import com.odoru.competitionservice.repository.CompetitionResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class handling business logic for competition planning.
 */
@Service
@RequiredArgsConstructor
public class CompetitionService {

  /** Minimum days in advance to schedule a competition. */
  private static final int MIN_DAYS_IN_ADVANCE = 7;

  /** Maximum allowed score. */
  private static final double MAX_SCORE = 10.0;

  /** Precision tolerance for score matching 1/10th decimal. */
  private static final double SCORE_PRECISION_TOLERANCE = 1e-9;

  /** Competition repository. */
  private final CompetitionRepository competitionRepository;

  /** Competition result repository. */
  private final CompetitionResultRepository competitionResultRepository;

  /** Member service client. */
  private final MemberClient memberClient;

  /**
   * Creates a new competition after validating all business rules.
   *
   * @param competition the competition details
   * @return the saved competition
   */
  public Competition createCompetition(final Competition competition) {
    // 1. Date check: must be >= 7 days in advance
    final LocalDate minAllowedDate = LocalDate.now()
        .plusDays(MIN_DAYS_IN_ADVANCE);
    if (competition.getDateTime().toLocalDate().isBefore(minAllowedDate)) {
      throw new IllegalArgumentException(
          "Competition date must be at least 7 days in the future");
    }

    // 2. Fetch and validate organizer teacher
    final MemberDto teacher = memberClient.getMemberById(
        competition.getTeacherId());

    if (teacher.getRole() != MemberRole.TEACHER) {
      throw new IllegalArgumentException(
          "Assigned organizer is not a teacher");
    }

    if (teacher.getExpertiseLevel() < competition.getTargetLevel()) {
      throw new IllegalArgumentException(
          "Teacher organizer is not qualified for level "
              + competition.getTargetLevel());
    }

    return competitionRepository.save(competition);
  }

  /**
   * Retrieves a specific competition by its ID.
   *
   * @param id the unique identifier of the competition
   * @return the competition details
   */
  public Competition getCompetitionById(final String id) {
    return competitionRepository.findById(id)
        .orElseThrow(() -> new RuntimeException(
            "Competition not found with id: " + id));
  }

  /**
   * Retrieves all competitions.
   *
   * @return the list of all competitions
   */
  public List<Competition> getAllCompetitions() {
    return competitionRepository.findAll();
  }

  /**
   * Retrieves competitions by target level.
   *
   * @param level the target level
   * @return the list of competitions matching the target level
   */
  public List<Competition> getCompetitionsByLevel(final int level) {
    return competitionRepository.findByTargetLevel(level);
  }

  /**
   * Retrieves competitions organized by a specific teacher.
   *
   * @param teacherId the unique identifier of the teacher
   * @return the list of competitions organized by the teacher
   */
  public List<Competition> getCompetitionsByTeacher(final String teacherId) {
    return competitionRepository.findByTeacherId(teacherId);
  }

  /**
   * Retrieves competitions for a student based on their expertise level.
   *
   * @param studentId the unique identifier of the student
   * @return the list of competitions matching the student's level
   */
  public List<Competition> getCompetitionsForStudent(final String studentId) {
    final MemberDto student = memberClient.getMemberById(studentId);
    return getCompetitionsByLevel(student.getExpertiseLevel());
  }

  /**
   * Registers or updates a student result for a competition.
   *
   * @param competitionId the unique identifier of the competition
   * @param studentId the unique identifier of the student
   * @param score the score obtained (0.0 to 10.0, 1/10th precision)
   * @param teacherId the unique identifier of the inputting teacher
   * @return the saved competition result
   */
  public CompetitionResult addOrUpdateResult(
      final String competitionId,
      final String studentId,
      final Double score,
      final String teacherId) {

    // 1. Validate score constraints (range and 1/10th decimal precision)
    if (score < 0.0 || score > MAX_SCORE) {
      throw new IllegalArgumentException(
          "Score must be between 0.0 and 10.0");
    }
    final double rounded = Math.round(score * 10.0) / 10.0;
    if (Math.abs(score - rounded) > SCORE_PRECISION_TOLERANCE) {
      throw new IllegalArgumentException(
          "Score precision must be at most 1/10th of a point");
    }

    // 2. Validate inputting teacher
    final MemberDto teacher = memberClient.getMemberById(teacherId);
    if (teacher.getRole() != MemberRole.TEACHER) {
      throw new IllegalArgumentException(
          "Only teachers can register results");
    }

    // 3. Verify competition and student eligibility
    final Competition competition = getCompetitionById(competitionId);
    final MemberDto student = memberClient.getMemberById(studentId);

    if (student.getExpertiseLevel() != competition.getTargetLevel()) {
      throw new IllegalArgumentException(
          "Student expertise level does not match the competition level");
    }

    // 4. Save result
    final CompetitionResult result = competitionResultRepository
        .findByCompetitionIdAndStudentId(competitionId, studentId)
        .orElseGet(() -> CompetitionResult.builder()
            .competitionId(competitionId)
            .studentId(studentId)
            .build());

    result.setScore(score);
    result.setTeacherId(teacherId);

    return competitionResultRepository.save(result);
  }

  /**
   * Retrieves all results achieved by a student.
   *
   * @param studentId the unique identifier of the student
   * @return the list of results
   */
  public List<CompetitionResult> getStudentResults(final String studentId) {
    memberClient.getMemberById(studentId); // Verify student exists
    return competitionResultRepository.findByStudentId(studentId);
  }
}
