package com.dymbia.sumaqagro.platform.iam.infrastructure.authorization.sfs.model;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

/**
 * Builds the Spring Security authentication for a verified bearer request.
 */
public final class UsernamePasswordAuthenticationTokenBuilder {

    private UsernamePasswordAuthenticationTokenBuilder() {
    }

    /**
     * Creates an authenticated token from an account whose bearer JWT was verified.
     *
     * @param principal the account and its role authorities
     * @param request the current HTTP request
     * @return the authenticated request token with credentials erased
     */
    public static UsernamePasswordAuthenticationToken build(
            UserDetails principal,
            HttpServletRequest request
    ) {
        var authentication = UsernamePasswordAuthenticationToken.authenticated(
                principal, null, principal.getAuthorities()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        authentication.eraseCredentials();
        return authentication;
    }
}