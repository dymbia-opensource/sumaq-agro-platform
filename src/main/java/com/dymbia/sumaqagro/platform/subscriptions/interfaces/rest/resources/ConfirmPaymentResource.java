package com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Request body to confirm a Mercado Pago payment after the producer comes back from the checkout.
 *
 * @param paymentId the {@code payment_id} that Mercado Pago adds to the return URL
 * @since 1.1
 */
@Schema(name = "ConfirmPaymentRequest", description = "Mercado Pago payment to confirm")
public record ConfirmPaymentResource(
        @Schema(description = "payment_id received in the Mercado Pago return URL", example = "1325897766")
        @NotBlank @Pattern(regexp = "\\d{1,30}") String paymentId) {
}
