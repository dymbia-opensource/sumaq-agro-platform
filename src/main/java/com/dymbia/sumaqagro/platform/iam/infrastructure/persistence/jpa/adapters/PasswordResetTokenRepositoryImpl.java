package com.dymbia.sumaqagro.platform.iam.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.iam.domain.model.entities.PasswordResetToken;
import com.dymbia.sumaqagro.platform.iam.domain.repositories.PasswordResetTokenRepository;
import com.dymbia.sumaqagro.platform.iam.infrastructure.persistence.jpa.assemblers.PasswordResetTokenPersistenceAssembler;
import com.dymbia.sumaqagro.platform.iam.infrastructure.persistence.jpa.repositories.PasswordResetTokenPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.time.Instant;

/** Implements reset token storage through Spring Data JPA. */
@Repository
public class PasswordResetTokenRepositoryImpl implements PasswordResetTokenRepository {

    @Override
    public boolean consumeIfUsable(String tokenHash, Instant consumedAt) {
        return persistenceRepository.consumeIfUsable(tokenHash, consumedAt) == 1;
    }

    private final PasswordResetTokenPersistenceRepository persistenceRepository;

    public PasswordResetTokenRepositoryImpl(
            PasswordResetTokenPersistenceRepository persistenceRepository
    ) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public void insert(PasswordResetToken token) {
        persistenceRepository.save(
                PasswordResetTokenPersistenceAssembler.toPersistenceFromDomain(token)
        );
    }

    @Override
    public Optional<PasswordResetToken> findByTokenHash(String tokenHash) {
        return persistenceRepository.findByTokenHash(tokenHash)
                .map(PasswordResetTokenPersistenceAssembler::toDomainFromPersistence);
    }
}
