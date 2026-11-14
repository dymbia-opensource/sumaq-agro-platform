package com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.BillingCycle;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PlanCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

/** A catalog plan identified by its stable commercial code. */
@Getter
public class Plan {

    private final PlanCode code;
    private final BigDecimal monthlyPrice;
    private final BigDecimal annualPrice;
    private final int registeredPlotLimit;
    private final int producerLimit;
    private final int supervisedPlotLimit;

    public Plan(
            PlanCode code,
            BigDecimal monthlyPrice,
            BigDecimal annualPrice,
            int registeredPlotLimit,
            int producerLimit,
            int supervisedPlotLimit
    ) {
        this.code = Objects.requireNonNull(code, "Plan code is required");
        this.monthlyPrice = requirePositivePrice(monthlyPrice);
        this.annualPrice = requirePositivePrice(annualPrice);

        if (registeredPlotLimit < 0 || producerLimit < 0 || supervisedPlotLimit < 0) {
            throw new IllegalArgumentException("Plan limits cannot be negative");
        }

        this.registeredPlotLimit = registeredPlotLimit;
        this.producerLimit = producerLimit;
        this.supervisedPlotLimit = supervisedPlotLimit;
    }

    /** Returns the catalog price in PEN for the selected billing cycle. */
    public BigDecimal priceFor(BillingCycle cycle) {
        return switch (Objects.requireNonNull(cycle, "Billing cycle is required")) {
            case MONTHLY -> monthlyPrice;
            case ANNUAL -> annualPrice;
        };
    }

    private static BigDecimal requirePositivePrice(BigDecimal price) {
        Objects.requireNonNull(price, "Plan price is required");
        if (price.signum() <= 0) {
            throw new IllegalArgumentException("Plan price must be positive");
        }
        return price;
    }
}