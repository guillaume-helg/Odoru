package com.odoru.badgeservice.client;

import com.odoru.badgeservice.dto.LessonDto;
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

  private final RestClient restClient;

  public LessonClient(
      @Value("${odoru.lesson-service.url}") final String lessonServiceUrl) {
    this.restClient = RestClient.builder()
        .baseUrl(lessonServiceUrl)
        .requestInterceptor(new JwtPropagationInterceptor())
        .build();
  }

  /**
   * Retrieves a lesson's details by its ID from Lesson Service.
   *
   * @throws RuntimeException if the lesson is not found or connection fails
   */
  public LessonDto getLessonById(final String lessonId) {
    try {
      return restClient.get()
          .uri("/api/lessons/{id}", lessonId)
          .retrieve()
          .body(LessonDto.class);
    } catch (RestClientResponseException ex) {
      if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new RuntimeException(
            "Lesson not found with id: " + lessonId, ex);
      }
      throw new RuntimeException(
          "Error communicating with Lesson Service", ex);
    } catch (Exception ex) {
      throw new RuntimeException("Lesson Service is unavailable", ex);
    }
  }
}
