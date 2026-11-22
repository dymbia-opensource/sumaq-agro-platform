package com.dymbia.sumaqagro.platform.subscriptions.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import com.dymbia.sumaqagro.platform.subscriptions.application.commandservices.SubscriptionCommandService;
import com.dymbia.sumaqagro.platform.subscriptions.application.internal.outboundservices.payments.PaymentGateway;
import com.dymbia.sumaqagro.platform.subscriptions.application.internal.outboundservices.payments.PaymentGateway.ProviderPayment;
import com.dymbia.sumaqagro.platform.subscriptions.domain.commands.CancelSubscriptionCommand;
import com.dymbia.sumaqagro.platform.subscriptions.domain.commands.ConfirmPaymentCommand;
import com.dymbia.sumaqagro.platform.subscriptions.domain.commands.PaymentCommand;
import com.dymbia.sumaqagro.platform.subscriptions.domain.commands.StartPaymentCheckoutCommand;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.aggregates.Subscription;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities.Payment;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.BillingCycle;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PaymentStatus;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PlanCode;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.SubscriptionStatus;
import com.dymbia.sumaqagro.platform.subscriptions.domain.repositories.PaymentRepository;
import com.dymbia.sumaqagro.platform.subscriptions.domain.repositories.PlanRepository;
import com.dymbia.sumaqagro.platform.subscriptions.domain.repositories.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

/** Records checkout outcomes and activates or renews approved subscriptions. */
@Service
public class SubscriptionCommandServiceImpl implements SubscriptionCommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionCommandServiceImpl.class);
    private static final ZoneId BILLING_ZONE = ZoneId.of("America/Lima");
    static final String CURRENCY = "PEN";
    private static final String PLAN_MISMATCH_REASON = "The selected plan must match the existing subscription";

    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;

    public SubscriptionCommandServiceImpl(
            PlanRepository planRepository,
            SubscriptionRepository subscriptionRepository,
            PaymentRepository paymentRepository,
            PaymentGateway paymentGateway
    ) {
        this.planRepository = planRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.paymentRepository = paymentRepository;
        this.paymentGateway = paymentGateway;
    }

    @Override
    @Transactional
    public Result<Subscription, ApplicationError> handle(CancelSubscriptionCommand command) {
        var existing = subscriptionRepository.findByUserIdForUpdate(command.userId());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound(
                    "subscription", Long.toString(command.userId())));
        }

        var subscription = existing.get();
        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            return Result.success(subscription);
        }

        subscription.cancelRenewal();
        return Result.success(subscriptionRepository.save(subscription));
    }

    @Override
    @Transactional
    public Result<Payment, ApplicationError> handle(PaymentCommand command) {
        if (command.simulatedOutcome() == PaymentStatus.PENDING) {
            return Result.failure(ApplicationError.validationError(
                    "simulatedOutcome", "The payment simulator only accepts COMPLETED or REJECTED"));
        }
        var plan = planRepository.findByCode(command.selectedPlan());
        if (plan.isEmpty()) {
            return Result.failure(ApplicationError.notFound("plan", command.selectedPlan().name()));
        }
        var existing = subscriptionRepository.findByUserIdForUpdate(command.userId());
        if (existing.isPresent() && existing.get().getPlanCode() != command.selectedPlan()) {
            return Result.failure(ApplicationError.conflict("subscription", PLAN_MISMATCH_REASON));
        }

        var now = Instant.now();
        var amount = plan.get().priceFor(command.billingCycle());
        Long subscriptionId = existing.map(Subscription::getId).orElse(null);
        if (command.simulatedOutcome() == PaymentStatus.COMPLETED) {
            subscriptionId = activateOrRenew(existing, command.userId(), command.selectedPlan(),
                    command.billingCycle(), now);
        }

        var payment = new Payment(
                "demo-" + UUID.randomUUID(), command.userId(), subscriptionId,
                command.selectedPlan(), command.billingCycle(), amount,
                command.simulatedOutcome(), now, null
        );
        paymentRepository.insert(payment);
        return Result.success(payment);
    }

    // Not transactional: no database connection is held while Mercado Pago answers
    @Override
    public Result<PaymentCheckout, ApplicationError> handle(StartPaymentCheckoutCommand command) {
        var plan = planRepository.findByCode(command.selectedPlan());
        if (plan.isEmpty()) {
            return Result.failure(ApplicationError.notFound("plan", command.selectedPlan().name()));
        }
        var existing = subscriptionRepository.findByUserId(command.userId());
        if (existing.isPresent() && existing.get().getPlanCode() != command.selectedPlan()) {
            return Result.failure(ApplicationError.conflict("subscription", PLAN_MISMATCH_REASON));
        }

        var reference = "mp-" + UUID.randomUUID();
        var amount = plan.get().priceFor(command.billingCycle());
        var title = "SumaqAgro - Plan %s (%s)".formatted(command.selectedPlan().name(),
                command.billingCycle() == BillingCycle.MONTHLY ? "mensual" : "anual");
        PaymentGateway.CheckoutSession checkout;
        try {
            checkout = paymentGateway.createCheckout(
                    new PaymentGateway.CheckoutRequest(reference, title, amount, CURRENCY));
        } catch (PaymentGateway.Unavailable exception) {
            LOGGER.warn("The checkout of user {} was not created: {}", command.userId(), exception.getMessage());
            return Result.failure(ApplicationError.unexpected("start-payment-checkout", exception.getMessage()));
        }

        // The payment is recorded only when the checkout exists, so there are no orphan pending payments
        paymentRepository.insert(Payment.pending(reference, command.userId(), command.selectedPlan(),
                command.billingCycle(), amount, Instant.now()));
        return Result.success(new PaymentCheckout(reference, amount, CURRENCY, checkout.checkoutUrl(),
                checkout.sandboxCheckoutUrl()));
    }

    @Override
    @Transactional
    public Result<Payment, ApplicationError> handle(ConfirmPaymentCommand command) {
        Optional<ProviderPayment> providerPayment;
        try {
            providerPayment = paymentGateway.findPayment(command.providerPaymentId());
        } catch (PaymentGateway.Unavailable exception) {
            return Result.failure(ApplicationError.unexpected("confirm-payment", exception.getMessage()));
        }
        if (providerPayment.isEmpty() || providerPayment.get().reference() == null) {
            return Result.failure(ApplicationError.notFound("payment", command.providerPaymentId()));
        }

        var found = paymentRepository.findByReferenceForUpdate(providerPayment.get().reference());
        // Another producer's payment answers 404, so it does not reveal that the payment exists
        if (found.isEmpty() || (command.requestedByUserId() != null
                && found.get().userId() != command.requestedByUserId())) {
            return Result.failure(ApplicationError.notFound("payment", command.providerPaymentId()));
        }
        var payment = found.get();
        if (!payment.isPending()) {
            return Result.success(payment);
        }

        var provider = providerPayment.get();
        return switch (provider.status()) {
            case PENDING -> Result.success(payment);
            case REJECTED -> {
                var rejected = payment.reject(provider.providerPaymentId());
                paymentRepository.update(rejected);
                yield Result.success(rejected);
            }
            case APPROVED -> approve(payment, provider);
        };
    }

    // An approved payment is only applied when Mercado Pago charged exactly the price we asked for
    private Result<Payment, ApplicationError> approve(Payment payment, ProviderPayment provider) {
        if (provider.amount() == null || provider.amount().compareTo(payment.amount()) != 0
                || !CURRENCY.equals(provider.currency())) {
            LOGGER.warn("Payment {} was approved with an unexpected amount or currency; it is not applied",
                    payment.reference());
            return Result.failure(ApplicationError.conflict("payment", "The charged amount does not match the plan"));
        }
        var existing = subscriptionRepository.findByUserIdForUpdate(payment.userId());
        if (existing.isPresent() && existing.get().getPlanCode() != payment.planCode()) {
            return Result.failure(ApplicationError.conflict("subscription", PLAN_MISMATCH_REASON));
        }
        var subscriptionId = activateOrRenew(existing, payment.userId(), payment.planCode(), payment.billingCycle(),
                Instant.now());
        var completed = payment.complete(subscriptionId, provider.providerPaymentId());
        paymentRepository.update(completed);
        return Result.success(completed);
    }

    private Long activateOrRenew(Optional<Subscription> existing, long userId, PlanCode planCode,
                                 BillingCycle billingCycle, Instant approvedAt) {
        var approvedOn = approvedAt.atZone(BILLING_ZONE).toLocalDate();
        Subscription subscription;
        if (existing.isPresent()) {
            subscription = existing.get();
            subscription.renew(billingCycle, approvedOn);
        } else {
            subscription = Subscription.activate(userId, planCode, billingCycle, approvedOn);
        }
        return subscriptionRepository.save(subscription).getId();
    }
}
