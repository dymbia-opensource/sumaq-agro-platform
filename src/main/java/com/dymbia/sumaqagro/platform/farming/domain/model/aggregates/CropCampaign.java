package com.dymbia.sumaqagro.platform.farming.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.StartCropCampaignCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.events.SowingDateRecordedEvent;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CampaignStatus;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CropType;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.FieldPlotId;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.SeedVariety;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Crop campaign aggregate root.
 * <p>
 * One production cycle of a crop on a field plot: crop type, seed variety, season and sowing date.
 * A campaign starts in progress and, once finished, accepts no more changes.
 * </p>
 * <p>
 * It is a plain domain class: the JPA mapping lives in {@code CropCampaignPersistenceEntity}.
 * </p>
 *
 * @see StartCropCampaignCommand
 * @since 1.0
 */
@Getter
public class CropCampaign extends AbstractDomainAggregateRoot<CropCampaign> {
    private static final String FINISHED_MESSAGE_KEY = "crop.campaign.error.finished";

    private Long id;
    private FieldPlotId fieldPlotId;
    private String season;
    private CropType cropType;
    private SeedVariety seedVariety;
    private boolean seedVarietyCustom;
    private LocalDate sowingDate;
    private CampaignStatus status;

    private CropCampaign(FieldPlotId fieldPlotId, String season, CropType cropType, SeedVariety seedVariety,
                         boolean seedVarietyCustom, LocalDate sowingDate) {
        this.fieldPlotId = required(fieldPlotId, "crop.campaign.error.plotId.invalid");
        this.season = required(season, "crop.campaign.error.season.invalid");
        this.cropType = required(cropType, "crop.campaign.error.cropType.required");
        this.seedVariety = required(seedVariety, "crop.campaign.error.seedVariety.notBlank");
        this.seedVarietyCustom = seedVarietyCustom;
        this.sowingDate = sowingDate;
        this.status = CampaignStatus.IN_PROGRESS;
    }

    /**
     * Starts a crop campaign from a start command. New campaigns are in progress.
     * When the command already brings the sowing date, the campaign raises {@link SowingDateRecordedEvent}.
     *
     * @param command command with the plot, season, crop type, seed variety and sowing date
     */
    public CropCampaign(StartCropCampaignCommand command) {
        this(new FieldPlotId(command.plotId()), command.season(), command.cropType(), command.seedVariety(),
                command.customSeedVariety(), command.sowingDate());
        if (sowingDate != null) {
            registerDomainEvent(new SowingDateRecordedEvent(fieldPlotId.fieldPlotId(), sowingDate));
        }
    }

    /**
     * Rebuilds a crop campaign that already exists in the database. It raises no events.
     *
     * @param id                the store id
     * @param fieldPlotId       the plot where the campaign takes place
     * @param season            the season, {@code YYYY-I} or {@code YYYY-II}
     * @param cropType          the type of crop
     * @param seedVariety       the seed variety
     * @param seedVarietyCustom whether the producer typed a variety that is not in the catalog
     * @param sowingDate        the sowing date, may be null
     * @param status            the campaign status
     * @return the crop campaign with the store state
     */
    public static CropCampaign reconstitute(Long id, FieldPlotId fieldPlotId, String season, CropType cropType,
                                            SeedVariety seedVariety, boolean seedVarietyCustom,
                                            LocalDate sowingDate, CampaignStatus status) {
        var cropCampaign = new CropCampaign(fieldPlotId, season, cropType, seedVariety, seedVarietyCustom,
                sowingDate);
        cropCampaign.id = id;
        cropCampaign.status = required(status, "crop.campaign.error.status.required");
        return cropCampaign;
    }

    /**
     * Changes the crop of the campaign.
     *
     * @param cropType the new crop type
     * @throws IllegalStateException when the campaign is finished
     */
    public void selectCropType(CropType cropType) {
        ensureInProgress();
        this.cropType = required(cropType, "crop.campaign.error.cropType.required");
    }

    /**
     * Changes the seed variety of the campaign.
     *
     * @param seedVariety the new seed variety
     * @param custom      whether the producer typed a variety that is not in the catalog
     * @throws IllegalStateException when the campaign is finished
     */
    public void specifySeedVariety(SeedVariety seedVariety, boolean custom) {
        ensureInProgress();
        this.seedVariety = required(seedVariety, "crop.campaign.error.seedVariety.notBlank");
        this.seedVarietyCustom = custom;
    }

    /**
     * Records the sowing date and raises {@link SowingDateRecordedEvent} (policy P4).
     *
     * @param sowingDate the sowing date
     * @throws IllegalStateException when the campaign is finished
     */
    public void recordSowingDate(LocalDate sowingDate) {
        ensureInProgress();
        this.sowingDate = required(sowingDate, "crop.campaign.error.sowingDate.required");
        registerDomainEvent(new SowingDateRecordedEvent(fieldPlotId.fieldPlotId(), sowingDate));
    }

    /**
     * Finishes the campaign. After this it accepts no more changes.
     *
     * @throws IllegalStateException when the campaign is already finished
     */
    public void close() {
        ensureInProgress();
        this.status = CampaignStatus.FINISHED;
    }

    /**
     * Tells whether the campaign still accepts changes and expenses.
     *
     * @return {@code true} when the campaign is in progress
     */
    public boolean isActive() {
        return status == CampaignStatus.IN_PROGRESS;
    }

    /**
     * Tells whether the campaign is finished.
     *
     * @return {@code true} when the campaign is finished
     */
    public boolean isFinished() {
        return status == CampaignStatus.FINISHED;
    }

    /**
     * Gets the id of the field plot of the campaign.
     *
     * @return the field plot id
     */
    public Long getFieldPlotIdValue() {
        return fieldPlotId.fieldPlotId();
    }

    // Invariant of the aggregate: a finished campaign is closed for changes
    private void ensureInProgress() {
        if (isFinished()) {
            throw new IllegalStateException(FINISHED_MESSAGE_KEY);
        }
    }

    // A missing value is a bad request: it answers 400 with the translated message of the key
    private static <T> T required(T value, String messageKey) {
        if (value == null) {
            throw new IllegalArgumentException(messageKey);
        }
        return value;
    }
}
