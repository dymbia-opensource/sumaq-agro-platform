package com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.resources;

import java.time.LocalDate;

/** REST representation of the current account's subscription. */
public record SubscriptionResource(
        Long id,
        String planCode,
        String billingCycle,
        String status,
        LocalDate startDate,
        LocalDate endDate
) {
}