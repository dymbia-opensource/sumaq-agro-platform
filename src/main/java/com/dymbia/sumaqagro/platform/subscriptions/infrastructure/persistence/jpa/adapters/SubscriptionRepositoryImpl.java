package com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.aggregates.Subscription;
import com.dymbia.sumaqagro.platform.subscriptions.domain.repositories.SubscriptionRepository;
import com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.assemblers.SubscriptionPersistenceAssembler;
import com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.repositories.SubscriptionPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Bridges the Subscription domain repository and Spring Data JPA. */
@Repository
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

    private final SubscriptionPersistenceRepository persistenceRepository;

    public SubscriptionRepositoryImpl(SubscriptionPersistenceRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public Optional<Subscription> findByUserId(long userId) {
        return persistenceRepository.findByUserId(userId)
                .map(SubscriptionPersistenceAssembler::toDomainFromPersistence);
    }
}