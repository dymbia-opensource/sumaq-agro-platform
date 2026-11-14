package com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.resources;

import java.math.BigDecimal;

/**
 * Public representation of a catalog plan , with prices and limits.
 */
public record PlanResource(
        String code,
        BigDecimal monthlyPrice,
        BigDecimal annualPrice,
        String currency,
        int registeredPlotLimit,
        int producerLimit,
        int supervisedPlotLimit
) {
}
