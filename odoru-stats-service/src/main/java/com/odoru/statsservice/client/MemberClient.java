package com.odoru.statsservice.client;

import java.util.Arrays;
import java.util.List;
import com.odoru.statsservice.dto.MemberDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class MemberClient {

  private final RestClient restClient;

  public MemberClient(
      @Value("${odoru.member-service.url}") final String memberServiceUrl) {
    this.restClient = RestClient.builder()
        .baseUrl(memberServiceUrl)
        .requestInterceptor(new JwtPropagationInterceptor())
        .build();
  }

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
