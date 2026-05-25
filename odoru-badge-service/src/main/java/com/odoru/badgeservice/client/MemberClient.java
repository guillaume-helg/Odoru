package com.odoru.badgeservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/**
 * REST client to communicate with the Member Service.
 */
@Component
public class MemberClient {

  /** RestClient instance. */
  private final RestClient restClient;

  /**
   * Constructs the MemberClient with the configured Member Service URL.
   *
   * @param memberServiceUrl the URL of the member service
   */
  public MemberClient(
      @Value("${odoru.member-service.url}") final String memberServiceUrl) {
    this.restClient = RestClient.builder()
        .baseUrl(memberServiceUrl)
        .requestInterceptor(new JwtPropagationInterceptor())
        .build();
  }

  /**
   * Verifies if a member exists by calling the Member Service.
   *
   * @param memberId the unique identifier of the member
   * @throws RuntimeException if the member is not found or connection fails
   */
  public void verifyMemberExists(final String memberId) {
    try {
      restClient.get()
          .uri("/api/members/{id}", memberId)
          .retrieve()
          .toBodilessEntity();
    } catch (RestClientResponseException ex) {
      if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new RuntimeException(
            "Member not found with id: " + memberId, ex);
      }
      throw new RuntimeException(
          "Error communicating with Member Service", ex);
    } catch (Exception ex) {
      throw new RuntimeException("Member Service is unavailable", ex);
    }
  }
}
