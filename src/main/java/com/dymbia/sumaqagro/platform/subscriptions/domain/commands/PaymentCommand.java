package com.dymbia.sumaqagro.platform.subscriptions.domain.commands;


import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.BillingCycle;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PaymentStatus;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PlanCode;

/**
 * Requests a simulated payment for the authenticated account.
 * The selected plan has been authorized at the checkout boundary.
 */
public record PaymentCommand(
        long userId,
        PlanCode selectedPlan,
        BillingCycle billingCycle,
        PaymentStatus simulatedOutcome
) {
}
