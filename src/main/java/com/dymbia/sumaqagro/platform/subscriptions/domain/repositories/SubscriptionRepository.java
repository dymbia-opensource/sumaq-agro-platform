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

    /**
     * Loads the account's subscription for an update inside a transaction.
     */
    Optional<Subscription> findByUserIdForUpdate(long userId);

    /**
     * Persists a subscription and returns it with its assigned identity.
     */
    Subscription save(Subscription subscription);

}
