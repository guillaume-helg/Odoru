package com.odoru.badgeservice.client;

import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtPropagationInterceptor implements ClientHttpRequestInterceptor {

  private final OAuth2AuthorizedClientManager authorizedClientManager;

  @Override
  public ClientHttpResponse intercept(
      final HttpRequest request,
      final byte[] body,
      final ClientHttpRequestExecution execution) throws IOException {

    final Authentication auth =
        SecurityContextHolder.getContext().getAuthentication();

    String tokenValue = null;

    if (auth instanceof JwtAuthenticationToken jwtToken) {
      tokenValue = jwtToken.getToken().getTokenValue();
    }

    if (tokenValue == null) {
      tokenValue = getM2mToken();
    }

    if (tokenValue != null) {
      request.getHeaders().setBearerAuth(tokenValue);
    }

    return execution.execute(request, body);
  }

  private String getM2mToken() {
    final OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
        .withClientRegistrationId("odoru-m2m")
        .principal("odoru-badge-service")
        .build();

    return Optional.ofNullable(authorizedClientManager.authorize(authorizeRequest))
        .map(OAuth2AuthorizedClient::getAccessToken)
        .map(OAuth2AccessToken::getTokenValue)
        .orElse(null);
  }
}
