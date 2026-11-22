package com.dymbia.sumaqagro.platform.subscriptions.domain.commands;

/**
 * Command to apply the outcome of a provider payment.
 * <p>
 * It only carries the provider payment id: the outcome is always read from the provider,
 * never trusted from the browser or the notification.
 * </p>
 *
 * @param providerPaymentId the payment id given by Mercado Pago (digits only)
 * @param requestedByUserId the producer that confirms from the browser, or {@code null} for a provider notification
 * @since 1.1
 */
public record ConfirmPaymentCommand(String providerPaymentId, Long requestedByUserId) {

    public ConfirmPaymentCommand {
        if (providerPaymentId == null || !providerPaymentId.strip().matches("\\d{1,30}")) {
            throw new IllegalArgumentException("Provider payment ID must be a number");
        }
        providerPaymentId = providerPaymentId.strip();
    }
}
