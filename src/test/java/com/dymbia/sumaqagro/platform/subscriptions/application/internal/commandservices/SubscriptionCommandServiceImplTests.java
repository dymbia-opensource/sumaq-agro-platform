package com.dymbia.sumaqagro.platform.subscriptions.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import com.dymbia.sumaqagro.platform.subscriptions.application.commandservices.SubscriptionCommandService.PaymentCheckout;
import com.dymbia.sumaqagro.platform.subscriptions.application.internal.outboundservices.payments.PaymentGateway;
import com.dymbia.sumaqagro.platform.subscriptions.application.internal.outboundservices.payments.PaymentGateway.ProviderPayment;
import com.dymbia.sumaqagro.platform.subscriptions.application.internal.outboundservices.payments.PaymentGateway.ProviderPaymentStatus;
import com.dymbia.sumaqagro.platform.subscriptions.domain.commands.ConfirmPaymentCommand;
import com.dymbia.sumaqagro.platform.subscriptions.domain.commands.StartPaymentCheckoutCommand;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.aggregates.Subscription;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities.Payment;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities.Plan;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.BillingCycle;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PaymentStatus;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PlanCode;
import com.dymbia.sumaqagro.platform.subscriptions.domain.repositories.PaymentRepository;
import com.dymbia.sumaqagro.platform.subscriptions.domain.repositories.PlanRepository;
import com.dymbia.sumaqagro.platform.subscriptions.domain.repositories.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SubscriptionCommandServiceImplTests {

    private static final long USER_ID = 5L;
    private static final BigDecimal SEED_MONTHLY = new BigDecimal("29.90");
    private static final String REFERENCE = "mp-1234";

    private final PlanRepository planRepository = mock(PlanRepository.class);
    private final SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);
    private final PaymentRepository paymentRepository = mock(PaymentRepository.class);
    private final PaymentGateway paymentGateway = mock(PaymentGateway.class);
    private final SubscriptionCommandServiceImpl service = new SubscriptionCommandServiceImpl(
            planRepository, subscriptionRepository, paymentRepository, paymentGateway);

    @BeforeEach
    void seedPlanAndNoSubscription() {
        when(planRepository.findByCode(PlanCode.SEED)).thenReturn(Optional.of(
                new Plan(PlanCode.SEED, SEED_MONTHLY, new BigDecimal("299.00"), 3, 0, 0)));
        when(subscriptionRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(subscriptionRepository.findByUserIdForUpdate(USER_ID)).thenReturn(Optional.empty());
        // The saved subscription gets an id, as the database would give it
        when(subscriptionRepository.save(any())).thenAnswer(invocation -> {
            Subscription subscription = invocation.getArgument(0);
            return Subscription.reconstitute(77L, subscription.getUserId(), subscription.getPlanCode(),
                    subscription.getBillingCycle(), subscription.getPeriod(), subscription.getStatus());
        });
    }

    private static Payment pendingPayment() {
        return Payment.pending(REFERENCE, USER_ID, PlanCode.SEED, BillingCycle.MONTHLY, SEED_MONTHLY, Instant.now());
    }

    private void mercadoPagoAnswers(ProviderPaymentStatus status, BigDecimal amount) {
        when(paymentGateway.findPayment("999")).thenReturn(Optional.of(
                new ProviderPayment("999", REFERENCE, status, amount, "PEN")));
    }

    private static ApplicationError errorOf(Result<?, ApplicationError> result) {
        return (ApplicationError) assertInstanceOf(Result.Failure.class, result).error();
    }

    private static Payment paymentOf(Result<Payment, ApplicationError> result) {
        return (Payment) assertInstanceOf(Result.Success.class, result).value();
    }

    @Test
    void startingACheckoutAsksMercadoPagoForThePlanPriceAndRecordsAPendingPayment() {
        when(paymentGateway.createCheckout(any())).thenReturn(new PaymentGateway.CheckoutSession(
                "pref-1", URI.create("https://www.mercadopago.com.pe/checkout/v1/redirect?pref_id=pref-1"), null));

        var result = service.handle(new StartPaymentCheckoutCommand(USER_ID, PlanCode.SEED, BillingCycle.MONTHLY));

        var request = ArgumentCaptor.forClass(PaymentGateway.CheckoutRequest.class);
        verify(paymentGateway).createCheckout(request.capture());
        assertEquals(SEED_MONTHLY, request.getValue().amount());
        assertEquals("PEN", request.getValue().currency());
        assertTrue(request.getValue().reference().startsWith("mp-"));

        var saved = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).insert(saved.capture());
        assertEquals(PaymentStatus.PENDING, saved.getValue().status());
        assertEquals(request.getValue().reference(), saved.getValue().reference());

        var checkout = (PaymentCheckout) assertInstanceOf(Result.Success.class, result).value();
        assertEquals("pref-1", checkout.checkoutUrl().getQuery().replace("pref_id=", ""));
    }

    @Test
    void anUnavailableMercadoPagoRecordsNoPayment() {
        when(paymentGateway.createCheckout(any())).thenThrow(new PaymentGateway.Unavailable("not configured"));

        var result = service.handle(new StartPaymentCheckoutCommand(USER_ID, PlanCode.SEED, BillingCycle.MONTHLY));

        assertInstanceOf(Result.Failure.class, result);
        verify(paymentRepository, never()).insert(any());
    }

    @Test
    void anApprovedPaymentActivatesTheSubscriptionAndCompletesThePayment() {
        mercadoPagoAnswers(ProviderPaymentStatus.APPROVED, new BigDecimal("29.9"));
        when(paymentRepository.findByReferenceForUpdate(REFERENCE)).thenReturn(Optional.of(pendingPayment()));

        var payment = paymentOf(service.handle(new ConfirmPaymentCommand("999", USER_ID)));

        assertEquals(PaymentStatus.COMPLETED, payment.status());
        assertEquals(77L, payment.subscriptionId());
        assertEquals("999", payment.providerPaymentId());
        verify(subscriptionRepository).save(any());
        verify(paymentRepository).update(payment);
    }

    @Test
    void confirmingAnAlreadyAppliedPaymentChangesNothing() {
        mercadoPagoAnswers(ProviderPaymentStatus.APPROVED, SEED_MONTHLY);
        var completed = pendingPayment().complete(77L, "999");
        when(paymentRepository.findByReferenceForUpdate(REFERENCE)).thenReturn(Optional.of(completed));

        var payment = paymentOf(service.handle(new ConfirmPaymentCommand("999", null)));

        assertEquals(PaymentStatus.COMPLETED, payment.status());
        verify(subscriptionRepository, never()).save(any());
        verify(paymentRepository, never()).update(any());
    }

    @Test
    void anotherProducersPaymentIsNotFound() {
        mercadoPagoAnswers(ProviderPaymentStatus.APPROVED, SEED_MONTHLY);
        when(paymentRepository.findByReferenceForUpdate(REFERENCE)).thenReturn(Optional.of(pendingPayment()));

        var result = service.handle(new ConfirmPaymentCommand("999", 42L));

        assertEquals("PAYMENT_NOT_FOUND", errorOf(result).code());
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    void anApprovedPaymentWithAnotherAmountIsNotApplied() {
        mercadoPagoAnswers(ProviderPaymentStatus.APPROVED, new BigDecimal("1.00"));
        when(paymentRepository.findByReferenceForUpdate(REFERENCE)).thenReturn(Optional.of(pendingPayment()));

        var result = service.handle(new ConfirmPaymentCommand("999", USER_ID));

        assertEquals("PAYMENT_CONFLICT", errorOf(result).code());
        verify(subscriptionRepository, never()).save(any());
        verify(paymentRepository, never()).update(any());
    }

    @Test
    void aRejectedPaymentIsRecordedWithoutSubscription() {
        mercadoPagoAnswers(ProviderPaymentStatus.REJECTED, SEED_MONTHLY);
        when(paymentRepository.findByReferenceForUpdate(REFERENCE)).thenReturn(Optional.of(pendingPayment()));

        var payment = paymentOf(service.handle(new ConfirmPaymentCommand("999", USER_ID)));

        assertEquals(PaymentStatus.REJECTED, payment.status());
        verify(subscriptionRepository, never()).save(any());
        verify(paymentRepository).update(payment);
    }

    @Test
    void aPaymentStillInProcessStaysPending() {
        mercadoPagoAnswers(ProviderPaymentStatus.PENDING, SEED_MONTHLY);
        when(paymentRepository.findByReferenceForUpdate(REFERENCE)).thenReturn(Optional.of(pendingPayment()));

        var payment = paymentOf(service.handle(new ConfirmPaymentCommand("999", USER_ID)));

        assertEquals(PaymentStatus.PENDING, payment.status());
        verify(paymentRepository, never()).update(any());
    }
}
