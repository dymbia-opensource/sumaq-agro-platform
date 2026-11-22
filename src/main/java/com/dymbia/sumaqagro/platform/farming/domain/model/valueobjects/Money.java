package com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Amount of money in a currency. Every operation returns a new value.
 *
 * @param amount   the amount, zero or positive, with two decimals
 * @param currency the ISO 4217 currency code, such as PEN
 * @since 1.1
 */
public record Money(BigDecimal amount, String currency) {
    public static final String DEFAULT_CURRENCY = "PEN";

    public Money {
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("money.error.amount.invalid");
        }
        if (currency == null || !currency.matches("^[A-Z]{3}$")) {
            throw new IllegalArgumentException("money.error.currency.invalid");
        }
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money zero(String currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public Money add(Money other) {
        ensureSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money multiply(BigDecimal factor) {
        return new Money(amount.multiply(factor), currency);
    }

    public Money divide(BigDecimal divisor) {
        return new Money(amount.divide(divisor, 2, RoundingMode.HALF_UP), currency);
    }

    private void ensureSameCurrency(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("money.error.currency.mismatch");
        }
    }
}
