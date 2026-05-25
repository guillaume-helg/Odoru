package com.odoru.statsservice.client;

import java.util.Arrays;
import java.util.List;
import com.odoru.statsservice.dto.LessonDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/**
 * REST client to communicate with the Lesson Service.
 */
@Component
public class LessonClient {

  /** RestClient instance. */
  private final RestClient restClient;

  /**
   * Constructs the LessonClient with the configured Lesson Service URL.
   *
   * @param lessonServiceUrl the URL of the lesson service
   */
  public LessonClient(
      @Value("${odoru.lesson-service.url}") final String lessonServiceUrl) {
    this.restClient = RestClient.builder()
        .baseUrl(lessonServiceUrl)
        .requestInterceptor(new JwtPropagationInterceptor())
        .build();
  }

  /**
   * Retrieves all scheduled lessons.
   *
   * @return the list of all lessons
   */
  public List<LessonDto> getAllLessons() {
    try {
      final LessonDto[] lessons = restClient.get()
          .uri("/api/lessons")
          .retrieve()
          .body(LessonDto[].class);
      return lessons != null ? Arrays.asList(lessons) : List.of();
    } catch (Exception ex) {
      throw new RuntimeException("Error fetching lessons", ex);
    }
  }

  /**
   * Retrieves a specific lesson's details by its ID.
   *
   * @param id the unique identifier of the lesson
   * @return the lesson details
   * @throws RuntimeException if the lesson is not found or connection fails
   */
  public LessonDto getLessonById(final String id) {
    try {
      return restClient.get()
          .uri("/api/lessons/{id}", id)
          .retrieve()
          .body(LessonDto.class);
    } catch (RestClientResponseException ex) {
      if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new RuntimeException(
            "Lesson not found with id: " + id, ex);
      }
      throw new RuntimeException(
          "Error communicating with Lesson Service", ex);
    } catch (Exception ex) {
      throw new RuntimeException("Lesson Service is unavailable", ex);
    }
  }
}
