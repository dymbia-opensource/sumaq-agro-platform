package com.dymbia.sumaqagro.platform.subscriptions.application.commandservices;

import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import com.dymbia.sumaqagro.platform.subscriptions.domain.commands.CancelSubscriptionCommand;
import com.dymbia.sumaqagro.platform.subscriptions.domain.commands.ConfirmPaymentCommand;
import com.dymbia.sumaqagro.platform.subscriptions.domain.commands.PaymentCommand;
import com.dymbia.sumaqagro.platform.subscriptions.domain.commands.StartPaymentCheckoutCommand;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.aggregates.Subscription;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities.Payment;

import java.math.BigDecimal;
import java.net.URI;

/** Handles subscription checkout commands. */
public interface SubscriptionCommandService {

    /** Records a demo payment with the outcome chosen by the caller (payment simulator). */
    Result<Payment, ApplicationError> handle(PaymentCommand command);

    /** Cancels renewal while preserving the account's paid access period. */
    Result<Subscription, ApplicationError> handle(CancelSubscriptionCommand command);

    /**
     * Starts paying a plan in Mercado Pago: records a pending payment and creates its checkout.
     *
     * @param command the producer, the plan and the billing cycle
     * @return the checkout to redirect the producer to, or an application error
     * @since 1.1
     */
    Result<PaymentCheckout, ApplicationError> handle(StartPaymentCheckoutCommand command);

    /**
     * Applies the outcome of a Mercado Pago payment. It reads the payment from Mercado Pago and checks
     * its reference, owner and amount; an approved payment activates or renews the subscription.
     * Applying the same payment twice changes nothing.
     *
     * @param command the provider payment id, and the producer when it comes from the browser
     * @return the payment with its outcome, or an application error
     * @since 1.1
     */
    Result<Payment, ApplicationError> handle(ConfirmPaymentCommand command);

    /**
     * Checkout created for a pending payment.
     *
     * @param reference          our payment reference
     * @param amount             the price of the plan
     * @param currency           the currency
     * @param checkoutUrl        the Mercado Pago page where the producer pays
     * @param sandboxCheckoutUrl the Mercado Pago test page, when Mercado Pago gives one
     */
    record PaymentCheckout(String reference, BigDecimal amount, String currency, URI checkoutUrl,
                           URI sandboxCheckoutUrl) {
    }
}
