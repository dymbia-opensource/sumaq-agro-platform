package com.dymbia.sumaqagro.platform.subscriptions.application.internal.outboundservices.payments;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Optional;

/**
 * Outbound port to the payment provider.
 * <p>
 * Subscriptions only knows this contract; the Mercado Pago adapter of the infrastructure implements it.
 * The provider never decides the subscription: the application asks it for the payment and checks it.
 * </p>
 *
 * @since 1.1
 */
public interface PaymentGateway {

    /**
     * Creates a checkout where the producer pays a plan.
     *
     * @param request what is paid
     * @return the checkout links
     * @throws Unavailable when the provider is not configured, cannot be reached or rejects the request
     */
    CheckoutSession createCheckout(CheckoutRequest request);

    /**
     * Reads a payment from the provider.
     *
     * @param providerPaymentId the payment id given by the provider
     * @return the payment, or empty when the provider does not know it
     * @throws Unavailable when the provider is not configured or cannot be reached
     */
    Optional<ProviderPayment> findPayment(String providerPaymentId);

    /**
     * What the producer pays.
     *
     * @param reference our payment reference; the provider sends it back with the payment
     * @param title     the text shown in the checkout
     * @param amount    the price of the plan
     * @param currency  ISO currency code, for example {@code PEN}
     */
    record CheckoutRequest(String reference, String title, BigDecimal amount, String currency) {
    }

    /**
     * The checkout created by the provider.
     *
     * @param providerCheckoutId the checkout id in the provider
     * @param checkoutUrl        the page where the producer pays
     * @param sandboxCheckoutUrl the test page of the provider, when it has one
     */
    record CheckoutSession(String providerCheckoutId, URI checkoutUrl, URI sandboxCheckoutUrl) {
    }

    /**
     * A payment as the provider sees it.
     *
     * @param providerPaymentId the payment id in the provider
     * @param reference         our payment reference
     * @param status            the outcome
     * @param amount            the charged amount
     * @param currency          the charged currency
     */
    record ProviderPayment(String providerPaymentId, String reference, ProviderPaymentStatus status,
                           BigDecimal amount, String currency) {
    }

    enum ProviderPaymentStatus {
        APPROVED,
        PENDING,
        REJECTED
    }

    final class Unavailable extends RuntimeException {
        public Unavailable(String reason) {
            super(reason);
        }
    }
}
