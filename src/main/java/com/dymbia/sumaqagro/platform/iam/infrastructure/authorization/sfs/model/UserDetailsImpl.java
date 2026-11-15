package com.dymbia.sumaqagro.platform.iam.infrastructure.authorization.sfs.model;

import com.dymbia.sumaqagro.platform.iam.domain.model.aggregates.User;
import com.dymbia.sumaqagro.platform.iam.domain.model.entities.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Represents an authenticated IAM account in Spring Security.
 */
@Getter
@NullMarked
@EqualsAndHashCode(of = "id")
public class UserDetailsImpl implements UserDetails, CredentialsContainer {

    private final Long id;
    private final String username;

    private @Nullable String password;

    private final boolean accountNonExpired = true;
    private final boolean accountNonLocked = true;
    private final boolean credentialsNonExpired = true;
    private final boolean enabled = true;
    private final Collection<? extends GrantedAuthority> authorities;

    private UserDetailsImpl(
            Long id,
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = List.copyOf(authorities);
    }

    /**
     * Builds security details from a persisted IAM account.
     *
     * @param user the account loaded from IAM persistence
     * @return the principal containing the user ID, email and role authorities
     */
    public static UserDetailsImpl build(User user) {
        var authorities = user.getRoles().stream()
                .map(Role::getStringName)
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new UserDetailsImpl(
                user.getId(),
                user.getEmail().value(),
                user.getPasswordHash(),
                authorities
        );
    }

    /**
     * Returns the stored hash while credentials are still present.
     *
     * @return the stored hash, or {@code null} after credentials are erased
     */
    @JsonIgnore
    @Override
    public @Nullable String getPassword() {
        return password;
    }

    /**
     * Removes the stored hash from the principal after authentication.
     */
    @Override
    public void eraseCredentials() {
        this.password = null;
    }
}