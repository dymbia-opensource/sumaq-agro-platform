package com.dymbia.sumaqagro.platform.iam.infrastructure.tokens.jwt.services;

import com.dymbia.sumaqagro.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

/**
 * Issues and verifies signed JWTs using the configured HMAC key.
 */
@Service
public class TokenServiceImpl implements BearerTokenService {

    private static final String BEARER_TOKEN_PREFIX = "Bearer ";

    /** {@inheritDoc} */
    @Override
    public String getBearerTokenFrom(HttpServletRequest request) {
        var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.startsWithIgnoreCase(authorization, BEARER_TOKEN_PREFIX)) {
            return null;
        }

        var token = authorization.substring(BEARER_TOKEN_PREFIX.length()).strip();
        return token.isEmpty() ? null : token;
    }

    @Value("${authorization.jwt.secret}")
    private String secret;

    @Value("${authorization.jwt.expiration.minutes}")
    private long expirationMinutes;

    /** {@inheritDoc} */
    @Override
    public String generateToken(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Token subject is required");
        }

        if (expirationMinutes <= 0) {
            throw new IllegalStateException("JWT expiration must be positive");
        }

        var issuedAt = Instant.now();
        var expiration = issuedAt.plus(expirationMinutes, ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /** {@inheritDoc} */
    @Override
    public String getEmailFromToken(String token) {
        var claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        var email = claims.getSubject();
        if (email == null || email.isBlank() || claims.getExpiration() == null) {
            throw new IllegalArgumentException("Token subject and expiration are required");
        }

        return email;
    }

    /** {@inheritDoc} */
    @Override
    public boolean validateToken(String token) {
        try {
            getEmailFromToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    private SecretKey getSigningKey() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT signing key is not configured");
        }

        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}