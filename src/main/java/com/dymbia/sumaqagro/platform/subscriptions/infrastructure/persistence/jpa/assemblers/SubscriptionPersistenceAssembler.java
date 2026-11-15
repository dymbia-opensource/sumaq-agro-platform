package com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.assemblers;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.aggregates.Subscription;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.SubscriptionPeriod;
import com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.entities.SubscriptionPersistenceEntity;

/** Maps a persisted subscription back to its domain aggregate. */
public final class SubscriptionPersistenceAssembler {

    /** Maps the aggregate to its JPA representation. */
    public static SubscriptionPersistenceEntity toPersistenceFromDomain(Subscription subscription) {
        var entity = new SubscriptionPersistenceEntity(
                subscription.getUserId(), subscription.getPlanCode(), subscription.getBillingCycle(),
                subscription.getStatus(), subscription.getPeriod().startDate(),
                subscription.getPeriod().endDate()
        );
        entity.setId(subscription.getId());
        return entity;
    }

    private SubscriptionPersistenceAssembler() {
    }

    /** Reconstitutes a subscription using its stored status and access dates. */
    public static Subscription toDomainFromPersistence(SubscriptionPersistenceEntity entity) {
        return Subscription.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getPlanCode(),
                entity.getBillingCycle(),
                new SubscriptionPeriod(entity.getStartDate(), entity.getEndDate()),
                entity.getStatus()
        );
    }
}
