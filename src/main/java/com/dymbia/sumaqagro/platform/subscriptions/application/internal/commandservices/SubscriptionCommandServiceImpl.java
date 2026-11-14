package com.dymbia.sumaqagro.platform.subscriptions.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import com.dymbia.sumaqagro.platform.subscriptions.application.commandservices.SubscriptionCommandService;
import com.dymbia.sumaqagro.platform.subscriptions.domain.commands.PaymentCommand;
import com.dymbia.sumaqagro.platform.subscriptions.domain.commands.CancelSubscriptionCommand;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.aggregates.Subscription;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities.Payment;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PaymentStatus;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.SubscriptionStatus;
import com.dymbia.sumaqagro.platform.subscriptions.domain.repositories.PaymentRepository;
import com.dymbia.sumaqagro.platform.subscriptions.domain.repositories.PlanRepository;
import com.dymbia.sumaqagro.platform.subscriptions.domain.repositories.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

/** Records the checkout outcome and activates approved first subscriptions. */
@Service
public class SubscriptionCommandServiceImpl implements SubscriptionCommandService {

    @Override
    @Transactional
    public Result<Subscription, ApplicationError> handle(CancelSubscriptionCommand command) {
        var existing = subscriptionRepository.findByUserId(command.userId());
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
    private static final ZoneId BILLING_ZONE = ZoneId.of("America/Lima");

    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;

    public SubscriptionCommandServiceImpl(
            PlanRepository planRepository,
            SubscriptionRepository subscriptionRepository,
            PaymentRepository paymentRepository
    ) {
        this.planRepository = planRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public Result<Payment, ApplicationError> handle(PaymentCommand command) {
        var plan = planRepository.findByCode(command.selectedPlan());
        if (plan.isEmpty()) {
            return Result.failure(ApplicationError.notFound("plan", command.selectedPlan().name()));
        }
        if (subscriptionRepository.findByUserId(command.userId()).isPresent()) {
            return Result.failure(ApplicationError.conflict(
                    "subscription", "The account already has a subscription"));
        }

        var now = Instant.now();
        var amount = plan.get().priceFor(command.billingCycle());
        Long subscriptionId = null;
        if (command.simulatedOutcome() == PaymentStatus.COMPLETED) {
            var subscription = Subscription.activate(
                    command.userId(), command.selectedPlan(), command.billingCycle(),
                    now.atZone(BILLING_ZONE).toLocalDate());
            subscriptionId = subscriptionRepository.save(subscription).getId();
        }

        var payment = new Payment(
                "demo-" + UUID.randomUUID(), command.userId(), subscriptionId,
                command.selectedPlan(), command.billingCycle(), amount,
                command.simulatedOutcome(), now
        );
        paymentRepository.insert(payment);
        return Result.success(payment);
    }
}
