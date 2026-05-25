package com.odoru.lessonservice.client;

import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Interceptor to propagate JWT authentication token to downstream services.
 */
public class JwtPropagationInterceptor implements ClientHttpRequestInterceptor {

  @Override
  public ClientHttpResponse intercept(
      final HttpRequest request,
      final byte[] body,
      final ClientHttpRequestExecution execution) throws IOException {

    final Authentication auth =
        SecurityContextHolder.getContext().getAuthentication();
    if (auth instanceof JwtAuthenticationToken jwtToken) {
      request.getHeaders().setBearerAuth(jwtToken.getToken().getTokenValue());
    }
    return execution.execute(request, body);
  }
}
