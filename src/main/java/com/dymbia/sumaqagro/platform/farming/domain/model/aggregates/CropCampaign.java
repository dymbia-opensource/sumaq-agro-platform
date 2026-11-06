package com.dymbia.sumaqagro.platform.farming.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.StartCropCampaignCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CampaignStatus;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CropType;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.SeedVariety;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Crop campaign aggregate root.
 * <p>
 * One production cycle of a crop on a plot. It starts in progress.
 * </p>
 * <p>
 * It is a plain domain class: the JPA mapping belongs to the infrastructure layer.
 * </p>
 *
 * @see StartCropCampaignCommand
 * @since 1.0
 */
@Getter
public class CropCampaign extends AbstractDomainAggregateRoot<CropCampaign> {
    private final Long id;
    private final Long plotId;
    private final String season;
    private final CropType cropType;
    private final SeedVariety seedVariety;
    private final boolean seedVarietyCustom;
    private final LocalDate sowingDate;
    private final CampaignStatus status;

    private CropCampaign(Long id, Long plotId, String season, CropType cropType, SeedVariety seedVariety,
                         boolean seedVarietyCustom, LocalDate sowingDate, CampaignStatus status) {
        this.id = id;
        this.plotId = plotId;
        this.season = season;
        this.cropType = cropType;
        this.seedVariety = seedVariety;
        this.seedVarietyCustom = seedVarietyCustom;
        this.sowingDate = sowingDate;
        this.status = status;
    }

    /**
     * Creates a crop campaign from a start command. New campaigns are in progress.
     *
     * @param command command with the plot, season, crop type, seed variety and sowing date
     */
    public CropCampaign(StartCropCampaignCommand command) {
        this(null, command.plotId(), command.season(), command.cropType(), command.seedVariety(),
                command.customSeedVariety(), command.sowingDate(), CampaignStatus.IN_PROGRESS);
    }

    /**
     * Rebuilds a crop campaign that already exists in the database.
     *
     * @param id                the store id
     * @param plotId            the plot where the campaign takes place
     * @param season            the season, {@code YYYY-I} or {@code YYYY-II}
     * @param cropType          the type of crop
     * @param seedVariety       the seed variety
     * @param seedVarietyCustom whether the seed variety is a custom one
     * @param sowingDate        the sowing date, may be null
     * @param status            the campaign status
     * @return the crop campaign with the store state
     */
    public static CropCampaign reconstitute(Long id, Long plotId, String season, CropType cropType,
                                            SeedVariety seedVariety, boolean seedVarietyCustom,
                                            LocalDate sowingDate, CampaignStatus status) {
        return new CropCampaign(id, plotId, season, cropType, seedVariety, seedVarietyCustom, sowingDate, status);
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
