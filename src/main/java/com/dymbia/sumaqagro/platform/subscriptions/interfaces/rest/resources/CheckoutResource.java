package com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.resources;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.BillingCycle;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PaymentStatus;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PlanCode;
import jakarta.validation.constraints.NotNull;

/** Checkout input. The optional outcome is used for development scenarios. */
public record CheckoutResource(
        @NotNull PlanCode planCode,
        @NotNull BillingCycle billingCycle,
        PaymentStatus simulatedOutcome
) {
}
