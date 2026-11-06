package com.dymbia.sumaqagro.platform.iam.infrastructure.authorization.sfs.pipeline;

import com.dymbia.sumaqagro.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Authenticates requests carrying a valid IAM bearer token.
 */
public class BearerAuthorizationRequestFilter extends OncePerRequestFilter {

    private final BearerTokenService tokenService;
    private final UserDetailsService userDetailsService;

    public BearerAuthorizationRequestFilter(
            BearerTokenService tokenService,
            UserDetailsService userDetailsService
    ) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Sets the request authentication after verifying the token and loading the account.
     *
     * @param request the incoming request
     * @param response the outgoing response
     * @param filterChain the remaining filters
     * @throws ServletException if servlet processing fails
     * @throws IOException if request or response I/O fails
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        var token = tokenService.getBearerTokenFrom(request);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                var email = tokenService.getEmailFromToken(token);
                var userDetails = userDetailsService.loadUserByUsername(email);
                var authentication = UsernamePasswordAuthenticationToken.authenticated(
                        userDetails, null, userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                authentication.eraseCredentials();

                var context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            } catch (JwtException | IllegalArgumentException | UsernameNotFoundException exception) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
