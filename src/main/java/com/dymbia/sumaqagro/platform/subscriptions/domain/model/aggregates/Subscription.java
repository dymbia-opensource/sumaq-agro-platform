package com.dymbia.sumaqagro.platform.subscriptions.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.BillingCycle;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PlanCode;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.SubscriptionPeriod;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.SubscriptionStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;

/** A user's subscription and its paid access period. */
@Getter
public class Subscription extends AbstractDomainAggregateRoot<Subscription> {

    private final Long id;
    private final long userId;
    private final PlanCode planCode;
    private BillingCycle billingCycle;
    private SubscriptionPeriod period;
    private SubscriptionStatus status;

    private Subscription(
            Long id,
            long userId,
            PlanCode planCode,
            BillingCycle billingCycle,
            SubscriptionPeriod period,
            SubscriptionStatus status
    ) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }

        this.id = id;
        this.userId = userId;
        this.planCode = Objects.requireNonNull(planCode, "Plan code is required");
        this.billingCycle = Objects.requireNonNull(billingCycle, "Billing cycle is required");
        this.period = Objects.requireNonNull(period, "Subscription period is required");
        this.status = Objects.requireNonNull(status, "Subscription status is required");
    }

    /** Creates the first subscription after a payment has been approved. */
    public static Subscription activate(
            long userId, PlanCode planCode, BillingCycle cycle, LocalDate startDate
    ) {
        return new Subscription(
                null, userId, planCode, cycle,
                SubscriptionPeriod.startingOn(startDate, cycle),
                SubscriptionStatus.ACTIVE
        );
    }

    /** Restores an existing subscription from persisted data. */
    public static Subscription reconstitute(
            long id,
            long userId,
            PlanCode planCode,
            BillingCycle cycle,
            SubscriptionPeriod period,
            SubscriptionStatus status
    ) {
        if (id <= 0) {
            throw new IllegalArgumentException("Subscription ID must be positive");
        }

        return new Subscription(id, userId, planCode, cycle, period, status);
    }

    /** Extends access after an approved renewal, preserving any remaining paid days. */
    public void renew(BillingCycle cycle, LocalDate approvedOn) {
        Objects.requireNonNull(cycle, "Billing cycle is required");
        Objects.requireNonNull(approvedOn, "Approval date is required");

        if (approvedOn.isAfter(period.endDate())) {
            period = SubscriptionPeriod.startingOn(approvedOn, cycle);
        } else {
            period = new SubscriptionPeriod(period.startDate(), period.endDate().plusMonths(cycle.months()));
        }

        billingCycle = cycle;
        status = SubscriptionStatus.ACTIVE;
    }

    /** Cancels renewal while preserving the current paid period. */
    public void cancelRenewal() {
        status = SubscriptionStatus.CANCELLED;
    }

    /** Checks whether the subscription grants access on the supplied date. */
    public boolean hasAccessOn(LocalDate date) {
        return period.includes(date);
    }
}
