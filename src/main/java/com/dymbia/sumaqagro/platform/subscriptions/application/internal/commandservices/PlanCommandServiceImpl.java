package com.dymbia.sumaqagro.platform.subscriptions.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.subscriptions.application.commandservices.PlanCommandService;
import com.dymbia.sumaqagro.platform.subscriptions.domain.commands.SeedPlansCommand;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities.Plan;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PlanCode;
import com.dymbia.sumaqagro.platform.subscriptions.domain.repositories.PlanRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Loads the default catalog without overwriting existing plans.
 */
@Service
public class PlanCommandServiceImpl implements PlanCommandService {

    private static final List<Plan> DEFAULT_PLANS = List.of(
            new Plan(PlanCode.SEED,
                    new BigDecimal("49.00"), new BigDecimal("490.00"), 4, 0, 0),
            new Plan(PlanCode.COOPERATIVE_PRO,
                    new BigDecimal("189.00"), new BigDecimal("1890.00"), 50, 50, 0),
            new Plan(PlanCode.TECHNICAL_ADVISOR,
                    new BigDecimal("89.00"), new BigDecimal("890.00"), 0, 0, 20)
    );

    private final PlanRepository planRepository;

    public PlanCommandServiceImpl(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    @Transactional
    public void handle(SeedPlansCommand command) {
        for (Plan plan : DEFAULT_PLANS) {
            if (!planRepository.existsByCode(plan.getCode())) {
                planRepository.insert(plan);
            }
        }
    }
}