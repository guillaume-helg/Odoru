package com.odoru.statsservice.client;

import java.util.Arrays;
import java.util.List;
import com.odoru.statsservice.dto.LessonDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BadgeClient {

  private final RestClient restClient;

  public BadgeClient(
      @Value("${odoru.badge-service.url}") final String badgeServiceUrl) {
    this.restClient = RestClient.builder()
        .baseUrl(badgeServiceUrl)
        .requestInterceptor(new JwtPropagationInterceptor())
        .build();
  }

  public List<String> getLessonAttendees(final String lessonId) {
    try {
      final String[] studentIds = restClient.get()
          .uri("/api/badges/attendance/lesson/{lessonId}", lessonId)
          .retrieve()
          .body(String[].class);
      return studentIds != null ? Arrays.asList(studentIds) : List.of();
    } catch (Exception ex) {
      throw new RuntimeException("Error fetching lesson attendees", ex);
    }
  }

  public List<LessonDto> getStudentAttendedLessons(final String studentId) {
    try {
      final LessonDto[] lessons = restClient.get()
          .uri("/api/badges/attendance/student/{studentId}", studentId)
          .retrieve()
          .body(LessonDto[].class);
      return lessons != null ? Arrays.asList(lessons) : List.of();
    } catch (Exception ex) {
      throw new RuntimeException("Error fetching student attended lessons", ex);
    }
  }
}
