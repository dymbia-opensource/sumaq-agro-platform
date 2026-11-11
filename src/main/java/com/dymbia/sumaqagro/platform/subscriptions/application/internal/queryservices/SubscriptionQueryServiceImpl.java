package com.dymbia.sumaqagro.platform.subscriptions.application.internal.queryservices;

import com.dymbia.sumaqagro.platform.subscriptions.application.queryservices.SubscriptionQueryService;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.aggregates.Subscription;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.queries.GetSubscriptionByUserIdQuery;
import com.dymbia.sumaqagro.platform.subscriptions.domain.repositories.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** Retrieves subscriptions through the domain repository. */
@Service
public class SubscriptionQueryServiceImpl implements SubscriptionQueryService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionQueryServiceImpl(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Optional<Subscription> handle(GetSubscriptionByUserIdQuery query) {
        return subscriptionRepository.findByUserId(query.userId());
    }
}