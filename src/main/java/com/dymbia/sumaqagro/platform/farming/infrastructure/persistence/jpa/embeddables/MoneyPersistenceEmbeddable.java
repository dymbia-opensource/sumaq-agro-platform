package com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.embeddables;

import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.Money;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * JPA mapping of the {@link Money} value object: an amount and its currency.
 *
 * @since 1.1
 */
@Embeddable
@Getter
@NoArgsConstructor
public class MoneyPersistenceEmbeddable {

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    /**
     * Creates the embeddable from the domain value object.
     *
     * @param money the domain money
     */
    public MoneyPersistenceEmbeddable(Money money) {
        this.amount = money.amount();
        this.currency = money.currency();
    }

    /**
     * Rebuilds the domain value object.
     *
     * @return the domain money
     */
    public Money toDomain() {
        return new Money(amount, currency);
    }
}
