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

    private final IamContextFacade iamContextFacade;

    public ExternalIamService(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    public boolean isAgronomist(Long userId) {
        return userId != null && userId > 0 && iamContextFacade.userHasRole(userId, AGRONOMIST_ROLE);
    }

    public Optional<String> fetchEmailByUserId(Long userId) {
        if (userId == null || userId <= 0) return Optional.empty();
        var email = iamContextFacade.fetchEmailByUserId(userId);
        return email == null || email.isBlank() ? Optional.empty() : Optional.of(email);
    }
}
