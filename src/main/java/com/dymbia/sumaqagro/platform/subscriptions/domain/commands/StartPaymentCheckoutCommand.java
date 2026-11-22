package com.dymbia.sumaqagro.platform.subscriptions.domain.commands;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.BillingCycle;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PlanCode;

import java.util.Objects;

/**
 * Command to start paying a plan in the payment provider checkout (Mercado Pago).
 *
 * @param userId       the producer that pays
 * @param selectedPlan the plan to pay
 * @param billingCycle monthly or annual
 * @since 1.1
 */
public record StartPaymentCheckoutCommand(long userId, PlanCode selectedPlan, BillingCycle billingCycle) {

    public StartPaymentCheckoutCommand {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        Objects.requireNonNull(selectedPlan, "Plan code is required");
        Objects.requireNonNull(billingCycle, "Billing cycle is required");
    }
}
