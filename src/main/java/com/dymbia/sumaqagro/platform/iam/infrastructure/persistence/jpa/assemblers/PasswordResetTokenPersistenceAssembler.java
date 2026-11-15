package com.dymbia.sumaqagro.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.dymbia.sumaqagro.platform.iam.domain.model.entities.PasswordResetToken;
import com.dymbia.sumaqagro.platform.iam.infrastructure.persistence.jpa.entities.PasswordResetTokenPersistenceEntity;

/** Maps password reset tokens between domain and persistence. */
public final class PasswordResetTokenPersistenceAssembler {

    private PasswordResetTokenPersistenceAssembler() {
    }

    public static PasswordResetTokenPersistenceEntity toPersistenceFromDomain(
            PasswordResetToken token
    ) {
        return new PasswordResetTokenPersistenceEntity(
                token.getUserId(),
                token.getTokenHash(),
                token.getExpiresAt(),
                token.getConsumedAt()
        );
    }

    public static PasswordResetToken toDomainFromPersistence(
            PasswordResetTokenPersistenceEntity entity
    ) {
        return PasswordResetToken.reconstitute(
                entity.getUserId(),
                entity.getTokenHash(),
                entity.getExpiresAt(),
                entity.getConsumedAt()
        );
    }
}