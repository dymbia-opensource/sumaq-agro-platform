package com.dymbia.sumaqagro.platform.subscriptions.application.acl;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities.Plan;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PlanCode;
import com.dymbia.sumaqagro.platform.subscriptions.domain.repositories.PlanRepository;
import com.dymbia.sumaqagro.platform.subscriptions.domain.repositories.SubscriptionRepository;
import com.dymbia.sumaqagro.platform.subscriptions.interfaces.acl.SubscriptionsContextFacade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

/** Resolves the plot quota from the paid Seed subscription and catalog. */
@Service
public class SubscriptionsContextFacadeImpl implements SubscriptionsContextFacade {
    private static final ZoneId BILLING_ZONE = ZoneId.of("America/Lima");
    private final SubscriptionRepository subscriptions;
    private final PlanRepository plans;

    public SubscriptionsContextFacadeImpl(SubscriptionRepository subscriptions, PlanRepository plans) {
        this.subscriptions = subscriptions;
        this.plans = plans;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<Integer> lockSeedPlotLimit(long userId) {
        return subscriptions.findByUserIdForUpdate(userId)
                .filter(subscription -> subscription.getPlanCode() == PlanCode.SEED)
                .filter(subscription -> subscription.hasAccessOn(LocalDate.now(BILLING_ZONE)))
                .flatMap(subscription -> plans.findByCode(subscription.getPlanCode()))
                .map(Plan::getRegisteredPlotLimit);
    }
}
