package com.odoru.statsservice.client;

import java.util.Arrays;
import java.util.List;
import com.odoru.statsservice.dto.CompetitionDto;
import com.odoru.statsservice.dto.CompetitionResultDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * REST client to communicate with the Competition Service.
 */
@Component
public class CompetitionClient {

  /** RestClient instance. */
  private final RestClient restClient;

  /**
   * Constructs the CompetitionClient with the configured Competition Service URL.
   *
   * @param competitionServiceUrl the URL of the competition service
   */
  public CompetitionClient(
      @Value("${odoru.competition-service.url}")
      final String competitionServiceUrl) {
    this.restClient = RestClient.builder()
        .baseUrl(competitionServiceUrl)
        .requestInterceptor(new JwtPropagationInterceptor())
        .build();
  }

  /**
   * Retrieves all scheduled competitions.
   *
   * @return the list of all competitions
   */
  public List<CompetitionDto> getAllCompetitions() {
    try {
      final CompetitionDto[] competitions = restClient.get()
          .uri("/api/competitions")
          .retrieve()
          .body(CompetitionDto[].class);
      return competitions != null ? Arrays.asList(competitions) : List.of();
    } catch (Exception ex) {
      throw new RuntimeException("Error fetching competitions", ex);
    }
  }

  /**
   * Retrieves all scores achieved by a student.
   *
   * @param studentId the unique identifier of the student
   * @return the list of competition results
   */
  public List<CompetitionResultDto> getStudentResults(final String studentId) {
    try {
      final CompetitionResultDto[] results = restClient.get()
          .uri("/api/competitions/results/student/{studentId}", studentId)
          .retrieve()
          .body(CompetitionResultDto[].class);
      return results != null ? Arrays.asList(results) : List.of();
    } catch (Exception ex) {
      throw new RuntimeException("Error fetching student results", ex);
    }
  }
}
