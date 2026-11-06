package com.dymbia.sumaqagro.platform.iam.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.iam.domain.model.entities.Role;
import com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects.EmailAddress;

import lombok.Getter;

import java.util.Objects;
import java.util.Set;

@Getter
public class User {
    private final Long id;
    private final EmailAddress email;
    private final String passwordHash;
    private final Set<Role> roles;

    public User(EmailAddress email, String passwordHash, Role role) {
        this(null, email, passwordHash, Set.of(role));
    }

    private User(Long id, EmailAddress email, String passwordHash, Set<Role> roles) {
        this.id = id;
        this.email = Objects.requireNonNull(email, "Email is required");
        this.passwordHash = Objects.requireNonNull(passwordHash, "Password hash is required");
        this.roles = Set.copyOf(roles);

        if (passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash must not be blank");
        }

        if (this.roles.isEmpty()) {
            throw new IllegalArgumentException("User must have at least one role");
        }
    }

    public static User reconstitute(
            long id,
            EmailAddress email,
            String passwordHash,
            Set<Role> roles
    ) {
        if (id <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }

        return new User(id, email, passwordHash, roles);
    }

}
