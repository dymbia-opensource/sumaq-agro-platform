package com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects;

/**
 * Defines the Lenght of a paid subscription period in calendar months.
 */
public enum BillingCycle {
    MONTHLY(1),
    ANNUAL(12);

    private final int months;

    BillingCycle(int months) {
        this.months = months;
    }

    /**
     * Returns the number of months in this billing cycle.
     */
    public int months(){
        return months;
    }
}
