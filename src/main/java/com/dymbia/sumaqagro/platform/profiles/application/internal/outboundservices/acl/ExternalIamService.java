package com.dymbia.sumaqagro.platform.profiles.application.internal.outboundservices.acl;

import com.dymbia.sumaqagro.platform.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Outbound ACL: what Profiles needs from IAM (Informe 4.7.1.3).
 *
 * @since 1.0
 */
@Service
public class ExternalIamService {

    private static final String AGRONOMIST_ROLE = "ROLE_AGRONOMIST";
    private static final String FARMER_ROLE = "ROLE_FARMER";

    private final IamContextFacade iamContextFacade;

    public ExternalIamService(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    public boolean isAgronomist(Long userId) {
        return userId != null && userId > 0 && iamContextFacade.userHasRole(userId, AGRONOMIST_ROLE);
    }

    /**
     * Finds the producer account that uses an email.
     *
     * @param email email typed by the director
     * @return the IAM user id, empty when no producer account uses it
     */
    public Optional<Long> findFarmerUserIdByEmail(String email) {
        return findUserIdByEmailWithRole(email, FARMER_ROLE);
    }

    /**
     * Finds the agronomist account that uses an email.
     *
     * @param email email of the invitation
     * @return the IAM user id, empty when no agronomist account uses it
     */
    public Optional<Long> findAgronomistUserIdByEmail(String email) {
        return findUserIdByEmailWithRole(email, AGRONOMIST_ROLE);
    }

    public boolean isFarmer(Long userId) {
        return userId != null && userId > 0 && iamContextFacade.userHasRole(userId, FARMER_ROLE);
    }

    private Optional<Long> findUserIdByEmailWithRole(String email, String role) {
        if (email == null || email.isBlank()) return Optional.empty();
        try {
            var userId = iamContextFacade.fetchUserIdByEmail(email.trim().toLowerCase(java.util.Locale.ROOT));
            return userId != null && userId > 0 && iamContextFacade.userHasRole(userId, role)
                    ? Optional.of(userId)
                    : Optional.empty();
        } catch (IllegalArgumentException invalidEmail) {
            return Optional.empty();
        }
    }

    public Optional<String> fetchEmailByUserId(Long userId) {
        if (userId == null || userId <= 0) return Optional.empty();
        var email = iamContextFacade.fetchEmailByUserId(userId);
        return email == null || email.isBlank() ? Optional.empty() : Optional.of(email);
    }
}
