package com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.entities;

import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CampaignStatus;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CropType;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.SeedVariety;
import com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.converters.SeedVarietyAttributeConverter;
import com.dymbia.sumaqagro.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * JPA persistence entity for crop campaigns.
 * <p>
 * The column names match the {@code crop_campaigns} table that already exists in the database.
 * </p>
 */
@Entity
@Table(name = "crop_campaigns")
@Getter
@Setter
@NoArgsConstructor
public class CropCampaignPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "plot_id", nullable = false)
    private Long plotId;

    @Column(name = "season", nullable = false, length = 10)
    private String season;

    @Enumerated(EnumType.STRING)
    @Column(name = "crop_type", nullable = false, length = 30)
    private CropType cropType;

    @Convert(converter = SeedVarietyAttributeConverter.class)
    @Column(name = "seed_variety", nullable = false, length = 60)
    private SeedVariety seedVariety;

    @Column(name = "seed_variety_custom", nullable = false)
    private boolean seedVarietyCustom;

    @Column(name = "sowing_date")
    private LocalDate sowingDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CampaignStatus status;
}
