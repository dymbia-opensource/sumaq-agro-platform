package com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.entities;

import com.dymbia.sumaqagro.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.BillingCycle;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PlanCode;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.SubscriptionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Stores one subscription per account, including its paid access period.
 */
@Entity
@Table(name = "subscriptions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubscriptionPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_code", nullable = false, length = 30)
    private PlanCode planCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle", nullable = false, length = 10)
    private BillingCycle billingCycle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionStatus status;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    public SubscriptionPersistenceEntity(
            long userId, PlanCode planCode, BillingCycle billingCycle,
            SubscriptionStatus status, LocalDate startDate, LocalDate endDate
    ) {
        this.userId = userId;
        this.planCode = planCode;
        this.billingCycle = billingCycle;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
