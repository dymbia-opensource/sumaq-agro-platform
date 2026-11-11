package com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.entities;

import com.dymbia.sumaqagro.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PlanCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** JPA representation of a catalog plan and its limits. */
@Entity
@Table(name = "plans")
@Getter

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 30)
    private PlanCode code;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal annualPrice;

    @Column(nullable = false)
    private int registeredPlotLimit;

    @Column(nullable = false)
    private int producerLimit;

    @Column(nullable = false)
    private int supervisedPlotLimit;

    /** Creates the persistence representation of a new plan. */
    public PlanPersistenceEntity(PlanCode code, BigDecimal monthlyPrice, BigDecimal annualPrice, int registeredPlotLimit, int producerLimit, int supervisedPlotLimit) {
        this.code = code;
        this.monthlyPrice = monthlyPrice;
        this.annualPrice = annualPrice;
        this.registeredPlotLimit = registeredPlotLimit;
        this.producerLimit = producerLimit;
        this.supervisedPlotLimit = supervisedPlotLimit;
    }
}