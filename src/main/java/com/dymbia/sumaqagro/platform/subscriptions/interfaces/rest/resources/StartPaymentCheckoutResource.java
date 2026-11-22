package com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.resources;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.BillingCycle;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PlanCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Request body to pay a plan with Mercado Pago.
 *
 * @param planCode     the plan; it must match the role of the producer
 * @param billingCycle monthly or annual
 * @since 1.1
 */
@Schema(name = "StartPaymentCheckoutRequest", description = "Plan to pay with Mercado Pago")
public record StartPaymentCheckoutResource(
        @Schema(description = "Plan to pay", example = "SEED") @NotNull PlanCode planCode,
        @Schema(description = "Billing cycle", example = "MONTHLY") @NotNull BillingCycle billingCycle) {
}
