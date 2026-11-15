package com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Calendar dates during which the subscription grants access.
 *
 * @param startDate first day of access
 * @param endDate last day of access, inclusive
 */
public record SubscriptionPeriod(LocalDate startDate, LocalDate endDate) {

    public SubscriptionPeriod {
        Objects.requireNonNull(startDate, "Start date is required");
        Objects.requireNonNull(endDate, "End date is required");

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot precede start date");
        }
    }

    /** Creates a paid period starting on the supplied date. */
    public static SubscriptionPeriod startingOn(LocalDate startDate, BillingCycle cycle) {
        Objects.requireNonNull(startDate, "Start date is required");
        Objects.requireNonNull(cycle, "Billing cycle is required");
        return new SubscriptionPeriod(startDate, startDate.plusMonths(cycle.months()));
    }

    /** Checks whether the supplied date is within the paid period. */
    public boolean includes(LocalDate date) {
        Objects.requireNonNull(date, "Date is required");
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}