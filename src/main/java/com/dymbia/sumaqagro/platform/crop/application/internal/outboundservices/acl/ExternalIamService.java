package com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.acl;

import com.dymbia.sumaqagro.platform.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Anti-corruption layer of Crop towards IAM. It only uses the IAM public facade.
 * <p>
 * The bean has its own name because Profiles also has an {@code ExternalIamService}: two beans with the same
 * default name stop the application from starting.
 * </p>
 *
 * @since 1.1
 */
@Service("cropExternalIamService")
public class ExternalIamService {

    private final IamContextFacade iamContextFacade;

    public ExternalIamService(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    /**
     * Finds the email of a producer, where the agroclimatic alerts are sent.
     *
     * @param userId the producer user id
     * @return the email, or empty when the user does not exist
     */
    public Optional<String> fetchEmailByUserId(Long userId) {
        var email = iamContextFacade.fetchEmailByUserId(userId);
        return email == null || email.isBlank() ? Optional.empty() : Optional.of(email);
    }
}
