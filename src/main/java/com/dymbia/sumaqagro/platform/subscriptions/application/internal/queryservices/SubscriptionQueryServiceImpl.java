package com.dymbia.sumaqagro.platform.subscriptions.application.internal.queryservices;

import com.dymbia.sumaqagro.platform.subscriptions.application.queryservices.SubscriptionQueryService;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.aggregates.Subscription;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities.Payment;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.queries.GetPaymentsByUserIdQuery;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.queries.GetSubscriptionByUserIdQuery;
import com.dymbia.sumaqagro.platform.subscriptions.domain.repositories.PaymentRepository;
import com.dymbia.sumaqagro.platform.subscriptions.domain.repositories.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/** Retrieves subscriptions through the domain repository. */
@Service
public class SubscriptionQueryServiceImpl implements SubscriptionQueryService {

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;

    public SubscriptionQueryServiceImpl(
            SubscriptionRepository subscriptionRepository,
            PaymentRepository paymentRepository
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Optional<Subscription> handle(GetSubscriptionByUserIdQuery query) {
        return subscriptionRepository.findByUserId(query.userId());
    }

    @Override
    public List<Payment> handle(GetPaymentsByUserIdQuery query) {
        return paymentRepository.findByUserId(query.userId());
    }
}
