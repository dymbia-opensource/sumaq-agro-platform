package com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.aggregates.Subscription;
import com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.resources.SubscriptionResource;

/** Converts the subscription aggregate into its REST representation. */
public final class SubscriptionResourceFromEntityAssembler {

    private SubscriptionResourceFromEntityAssembler() {
    }

    /** Includes the plan code and the first and last paid days. */
    public static SubscriptionResource toResourceFromEntity(Subscription subscription) {
        return new SubscriptionResource(
                subscription.getId(),
                subscription.getPlanCode().name(),
                subscription.getBillingCycle().name(),
                subscription.getStatus().name(),
                subscription.getPeriod().startDate(),
                subscription.getPeriod().endDate()
        );
    }
}