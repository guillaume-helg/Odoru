package com.odoru.statsservice.client;

import java.util.Arrays;
import java.util.List;
import com.odoru.statsservice.dto.MemberDto;
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
        .build();
  }

  /**
   * Retrieves a member's details by their ID.
   *
   * @param memberId the unique identifier of the member
   * @return the member details
   * @throws RuntimeException if the member is not found or connection fails
   */
  public MemberDto getMemberById(final String memberId) {
    try {
      return restClient.get()
          .uri("/api/members/{id}", memberId)
          .retrieve()
          .body(MemberDto.class);
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

  /**
   * Retrieves all members registered in the system.
   *
   * @return the list of all members
   */
  public List<MemberDto> getAllMembers() {
    try {
      final MemberDto[] members = restClient.get()
          .uri("/api/members")
          .retrieve()
          .body(MemberDto[].class);
      return members != null ? Arrays.asList(members) : List.of();
    } catch (Exception ex) {
      throw new RuntimeException("Error fetching members", ex);
    }
  }
}
