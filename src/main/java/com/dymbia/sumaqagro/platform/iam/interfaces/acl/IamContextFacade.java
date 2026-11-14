package com.dymbia.sumaqagro.platform.iam.interfaces.acl;

import com.dymbia.sumaqagro.platform.iam.domain.model.aggregates.User;
import com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects.EmailAddress;
import com.dymbia.sumaqagro.platform.iam.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;

/**
 * Public entry point of the IAM context for other bounded contexts (e.g. Profiles).
 *
 * <p>
 * IAM already depends on Profiles when a user signs up.</p>
 *
 * @since 1.0
 */
@Service
public class IamContextFacade {

    private final UserRepository userRepository;

    public IamContextFacade(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * @return the user id, or {@code 0L} when no user has that email
     */
    public Long fetchUserIdByEmail(String email) {
        if (email == null || email.isBlank()) return 0L;
        return userRepository.findByEmail(new EmailAddress(email))
                .map(User::getId)
                .orElse(0L);
    }

    /**
     * @return the email of the user, or an empty text when the user does not exist
     */
    public String fetchEmailByUserId(Long userId) {
        if (userId == null || userId <= 0) return "";
        return userRepository.findById(userId)
                .map(user -> user.getEmail().value())
                .orElse("");
    }

    public boolean userHasRole(Long userId, String roleName) {
        if (userId == null || userId <= 0 || roleName == null) return false;
        return userRepository.findById(userId)
                .map(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getStringName().equals(roleName)))
                .orElse(false);
    }
}
