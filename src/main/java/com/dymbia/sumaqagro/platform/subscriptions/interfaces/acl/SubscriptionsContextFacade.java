package com.dymbia.sumaqagro.platform.subscriptions.interfaces.acl;

import java.util.Optional;

/** Provides subscription limits to other bounded contexts. */
public interface SubscriptionsContextFacade {
    /** Resolves the current Seed limit and locks the subscription in the caller's transaction. */
    Optional<Integer> lockSeedPlotLimit(long userId);

    /** Resolves the member limit while holding the director's subscription lock. */
    Optional<Integer> lockCooperativeMemberLimit(long userId);
}
