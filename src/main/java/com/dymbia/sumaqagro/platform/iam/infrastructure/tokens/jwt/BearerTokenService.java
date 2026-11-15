package com.dymbia.sumaqagro.platform.iam.infrastructure.tokens.jwt;

import com.dymbia.sumaqagro.platform.iam.application.internal.outboundservices.tokens.TokenService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Extends the IAM token contract with HTTP bearer token extraction.
 */
public interface BearerTokenService extends TokenService {

    /**
     * Extracts the bearer token from the Authorization header.
     *
     * @param request the incoming HTTP request
     * @return the token, or {@code null} if no bearer token was provided
     */
    String getBearerTokenFrom(HttpServletRequest request);
}