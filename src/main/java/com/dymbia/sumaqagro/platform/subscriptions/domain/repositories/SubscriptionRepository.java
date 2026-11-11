package com.dymbia.sumaqagro.platform.subscriptions.domain.repositories;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.aggregates.Subscription;

import java.util.Optional;


/**
 * Persistence port for the Subscription aggregate
 */
public interface SubscriptionRepository {

    /**
     * Returns the subscription of the given account, if one exists.
     */
    Optional<Subscription> findByUserId(long userId);

}
