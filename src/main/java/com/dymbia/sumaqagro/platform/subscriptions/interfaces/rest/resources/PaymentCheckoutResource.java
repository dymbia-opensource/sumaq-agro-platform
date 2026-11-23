package com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.net.URI;

/**
 * Response body of a Mercado Pago checkout: the frontend redirects the producer to {@code checkoutUrl}.
 *
 * @param reference          our payment reference
 * @param amount             the price of the plan
 * @param currency           the currency
 * @param checkoutUrl        the Mercado Pago page where the producer pays
 * @param sandboxCheckoutUrl the Mercado Pago test page, when Mercado Pago gives one
 * @since 1.1
 */
@Schema(name = "PaymentCheckoutResponse", description = "Mercado Pago checkout of a pending payment")
public record PaymentCheckoutResource(
        @Schema(description = "Payment reference", example = "mp-3f1c...") String reference,
        @Schema(description = "Price of the plan", example = "29.90") BigDecimal amount,
        @Schema(description = "Currency", example = "PEN") String currency,
        @Schema(description = "Mercado Pago page where the producer pays") URI checkoutUrl,
        @Schema(description = "Mercado Pago test page, when available", nullable = true) URI sandboxCheckoutUrl) {
}
