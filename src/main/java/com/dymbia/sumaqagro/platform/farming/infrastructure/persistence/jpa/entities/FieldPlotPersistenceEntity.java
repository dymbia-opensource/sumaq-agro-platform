package com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.entities;

import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.PlotBoundary;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.PlotStatus;
import com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.converters.PlotBoundaryAttributeConverter;
import com.dymbia.sumaqagro.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * JPA persistence entity for field plots.
 */
@Entity
@Table(name = "field_plots", uniqueConstraints = {@UniqueConstraint(name =
        FieldPlotPersistenceEntity.OWNER_USER_ID_NAME_UNIQUE_CONSTRAINT, columnNames = {"owner_user_id", "name"})})
@Getter
@Setter
@NoArgsConstructor
public class FieldPlotPersistenceEntity extends AuditableAbstractPersistenceEntity {
    public static final String OWNER_USER_ID_NAME_UNIQUE_CONSTRAINT = "uk_owner_user_id_name";

    @Column(name = "owner_user_id", nullable = false)
    private Long ownerUserId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "region", nullable = false, length = 150)
    private String region;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PlotStatus status;

    @Column(name = "area_hectares", nullable = false, precision = 10, scale = 2)
    private BigDecimal areaHectares;

    @Column(name = "agro_monitoring_polygon_id")
    private String agroMonitoringPolygonId;

    @Convert(converter = PlotBoundaryAttributeConverter.class)
    @Column(name = "boundary", columnDefinition = "TEXT")
    private PlotBoundary boundary;
}