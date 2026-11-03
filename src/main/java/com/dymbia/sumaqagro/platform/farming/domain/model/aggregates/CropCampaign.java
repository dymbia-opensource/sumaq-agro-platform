package com.dymbia.sumaqagro.platform.farming.domain.model.aggregates;


import com.dymbia.sumaqagro.platform.farming.domain.model.commands.StartCropCampaignCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CampaignStatus;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CropType;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.SeedVariety;
import com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.converters.SeedVarietyAttributeConverter;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Crop campaign aggregate: one production cycle of a crop on a plot.
 * The creation and update dates come from the parent class.
 *
 * @see StartCropCampaignCommand
 * @since 1.0
 */
@Getter
@Entity
public class CropCampaign extends AuditableAbstractAggregateRoot<CropCampaign> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long plotId;

    @Column(nullable = false, length = 10)
    private String season;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CropType cropType;

    @Column(nullable = false, length = 60)
    @Convert(converter = SeedVarietyAttributeConverter.class)
    private SeedVariety seedVariety;

    @Column(nullable = false)
    private boolean seedVarietyCustom;

    private LocalDate sowingDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CampaignStatus status;

    protected CropCampaign() {}

    /**
     * Creates a crop campaign from a start command. New campaigns are in progress.
     *
     * @param command command with the plot, season, crop type, seed variety and sowing date
     */
    public CropCampaign(StartCropCampaignCommand command) {
        this.plotId = command.plotId();
        this.season = command.season();
        this.cropType = command.cropType();
        this.seedVariety = command.seedVariety();
        this.seedVarietyCustom = command.customSeedVariety();
        this.sowingDate = command.sowingDate();
        this.status = CampaignStatus.IN_PROGRESS;
    }

    /**
     * Tells whether the campaign still accepts changes and expenses.
     *
     * @return {@code true} when the campaign is in progress
     */
    public boolean isActive() {
        return status == CampaignStatus.IN_PROGRESS;
    }
}