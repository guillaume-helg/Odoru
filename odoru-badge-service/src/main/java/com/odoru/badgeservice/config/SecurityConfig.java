package com.odoru.badgeservice.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for Badge Service.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  /**
   * Configures the OAuth2AuthorizedClientManager for client credentials flow.
   */
  @Bean
  public OAuth2AuthorizedClientManager authorizedClientManager(
      final ClientRegistrationRepository clientRegistrationRepository,
      final OAuth2AuthorizedClientService authorizedClientService) {

    final OAuth2AuthorizedClientProvider authorizedClientProvider =
        OAuth2AuthorizedClientProviderBuilder.builder()
            .clientCredentials()
            .build();

    final AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
        new AuthorizedClientServiceOAuth2AuthorizedClientManager(
            clientRegistrationRepository, authorizedClientService);
    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

    return authorizedClientManager;
  }

  /**
   * Configures the security filter chain.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(final HttpSecurity http)
      throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/badges/scan").permitAll() // Device simulation
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**",
                "/swagger-ui.html").permitAll()
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth -> oauth
            .jwt(jwt -> jwt.jwtAuthenticationConverter(
                jwtAuthenticationConverter()))
        );
    return http.build();
  }

  /**
   * Configures the JWT authentication converter with custom role mapping.
   */
  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    final JwtAuthenticationConverter converter =
        new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(
        new KeycloakRoleConverter());
    converter.setPrincipalClaimName("preferred_username");
    return converter;
  }

  private static class KeycloakRoleConverter
      implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> convert(final Jwt jwt) {
      final Map<String, Object> realmAccess =
          jwt.getClaim("realm_access");
      if (realmAccess == null || realmAccess.isEmpty()) {
        return Collections.emptyList();
      }
      final List<String> roles = (List<String>) realmAccess.get("roles");
      if (roles == null) {
        return Collections.emptyList();
      }
      return roles.stream()
          .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
          .collect(Collectors.toList());
    }
  }
}
