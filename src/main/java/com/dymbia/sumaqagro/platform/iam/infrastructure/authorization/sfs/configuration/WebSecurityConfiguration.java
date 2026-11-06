package com.dymbia.sumaqagro.platform.iam.infrastructure.authorization.sfs.configuration;

import com.dymbia.sumaqagro.platform.iam.infrastructure.authorization.sfs.pipeline.BearerAuthorizationRequestFilter;
import com.dymbia.sumaqagro.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 * Configures stateless bearer authentication for the SumaqAgro API.
 */
@Configuration
@EnableMethodSecurity
public class WebSecurityConfiguration {

    /**
     * Configures public routes, bearer authentication, and browser access.
     *
     * @param http the Spring Security HTTP configuration
     * @param tokenService the bearer token service
     * @param userDetailsService the account and role loader
     * @param allowedOrigin the browser origin allowed to call the API
     * @return the configured security filter chain
     * @throws Exception if the security filter chain cannot be built
     */
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            BearerTokenService tokenService,
            UserDetailsService userDetailsService,
            @Value("${authorization.cors.allowed-origin}") String allowedOrigin
    ) throws Exception {
        var cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of(allowedOrigin));
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Accept-Language"));

        var bearerFilter = new BearerAuthorizationRequestFilter(tokenService, userDetailsService);

        return http
                .cors(configurer -> configurer.configurationSource(request -> cors))
                .csrf(configurer -> configurer.disable())
                .sessionManagement(configurer ->
                        configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(configurer ->
                        configurer.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(configurer -> configurer
                        .requestMatchers(HttpMethod.POST, "/api/v1/users", "/api/v1/tokens").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(bearerFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
