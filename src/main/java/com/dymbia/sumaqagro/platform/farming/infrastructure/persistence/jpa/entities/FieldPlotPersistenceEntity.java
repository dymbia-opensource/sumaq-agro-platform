package com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.entities;

import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.PlotBoundary;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.PlotStatus;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.SoilTexture;
import com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.converters.PlotBoundaryAttributeConverter;
import com.dymbia.sumaqagro.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    @Column(name = "cooperative_id", updatable = false)
    private Long cooperativeId;

    @Column(name = "cooperative_member_id", updatable = false)
    private Long cooperativeMemberId;

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

    // Soil analysis (SoilBaseline): the four columns are empty until it is recorded
    @Column(name = "soil_ph", precision = 4, scale = 2)
    private BigDecimal soilPh;

    @Enumerated(EnumType.STRING)
    @Column(name = "soil_texture", length = 50)
    private SoilTexture soilTexture;

    @Column(name = "soil_organic_matter_percentage", precision = 5, scale = 2)
    private BigDecimal soilOrganicMatterPercentage;

    @Column(name = "soil_recorded_at")
    private LocalDate soilRecordedAt;
}
