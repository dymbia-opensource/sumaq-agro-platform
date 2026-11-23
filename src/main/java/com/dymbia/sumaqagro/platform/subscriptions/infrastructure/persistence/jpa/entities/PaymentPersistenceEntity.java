package com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.entities;

import com.dymbia.sumaqagro.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.BillingCycle;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PaymentStatus;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PlanCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
@Getter

/** Stores the outcome and quoted price of a checkout attempt. */
@Entity
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PaymentPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false, unique = true, length = 50)
    private String reference;

    @Column(nullable = false)
    private long userId;

    private Long subscriptionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PlanCode planCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private BillingCycle billingCycle;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    // VARCHAR and not a MySQL ENUM: an ENUM column is not widened by ddl-auto when a status is added
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(nullable = false)
    private Instant attemptedAt;

    // Payment id in the provider (Mercado Pago); null for demo payments
    @Column(length = 40)
    private String providerPaymentId;

    /**
     * Applies the outcome of a pending payment.
     *
     * @param status            the new status
     * @param subscriptionId    the subscription activated by the payment, if any
     * @param providerPaymentId the payment id in the provider
     */
    public void applyOutcome(PaymentStatus status, Long subscriptionId, String providerPaymentId) {
        this.status = status;
        this.subscriptionId = subscriptionId;
        this.providerPaymentId = providerPaymentId;
    }
}
