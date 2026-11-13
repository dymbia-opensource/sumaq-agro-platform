package com.dymbia.sumaqagro.platform.farming.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.farming.application.commandservices.CropCampaignCommandService;
import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.CropCampaign;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.CloseCropCampaignCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.StartCropCampaignCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.UpdateCropCampaignCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CampaignStatus;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.FieldPlotId;
import com.dymbia.sumaqagro.platform.farming.domain.repositories.CropCampaignRepository;
import com.dymbia.sumaqagro.platform.farming.domain.repositories.FieldPlotRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * Implementation of the crop campaign command service.
 * <p>
 * It checks the rules that need the database: the plot must exist and it can only have one campaign
 * in progress. The aggregate keeps its own rule: a finished campaign accepts no more changes.
 * </p>
 */
@Service
public class CropCampaignCommandServiceImpl implements CropCampaignCommandService {
    private static final String CROP_CAMPAIGN = "CropCampaign";
    private static final String CAMPAIGN_IN_PROGRESS_REASON = "The plot already has a campaign in progress.";
    private static final String CAMPAIGN_FINISHED_REASON = "The campaign is finished and accepts no more changes.";

    private final CropCampaignRepository cropCampaignRepository;
    private final FieldPlotRepository fieldPlotRepository;

    public CropCampaignCommandServiceImpl(CropCampaignRepository cropCampaignRepository,
                                          FieldPlotRepository fieldPlotRepository) {
        this.cropCampaignRepository = cropCampaignRepository;
        this.fieldPlotRepository = fieldPlotRepository;
    }

    /**
     * {@inheritDoc}
     * <p>
     * A missing plot is {@code notFound} (404). A plot with a campaign in progress is a {@code conflict} (409).
     * </p>
     */
    @Override
    public Result<Long, ApplicationError> handle(StartCropCampaignCommand command) {
        if (fieldPlotRepository.findById(command.plotId()).isEmpty()) {
            return Result.failure(ApplicationError.notFound("FieldPlot", command.plotId().toString()));
        }
        if (cropCampaignRepository.existsByFieldPlotIdAndStatus(new FieldPlotId(command.plotId()),
                CampaignStatus.IN_PROGRESS)) {
            return Result.failure(ApplicationError.conflict(CROP_CAMPAIGN, CAMPAIGN_IN_PROGRESS_REASON));
        }
        var cropCampaign = new CropCampaign(command);
        try {
            cropCampaign = cropCampaignRepository.save(cropCampaign);
        } catch (Exception exception) {
            return Result.failure(ApplicationError.unexpected("start-crop-campaign", exception.getMessage()));
        }
        return Result.success(cropCampaign.getId());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Each value that is present goes through its method of the aggregate, so the sowing date
     * still raises {@code SowingDateRecordedEvent} (policy P4) only when it is sent.
     * </p>
     */
    @Override
    public Result<Long, ApplicationError> handle(UpdateCropCampaignCommand command) {
        return changeCampaignInProgress(command.cropCampaignId(), cropCampaign -> {
            if (command.cropType() != null) {
                cropCampaign.selectCropType(command.cropType());
            }
            if (command.seedVariety() != null) {
                cropCampaign.specifySeedVariety(command.seedVariety(), command.customSeedVariety());
            }
            if (command.sowingDate() != null) {
                cropCampaign.recordSowingDate(command.sowingDate());
            }
        });
    }

    @Override
    public Result<Long, ApplicationError> handle(CloseCropCampaignCommand command) {
        return changeCampaignInProgress(command.cropCampaignId(), CropCampaign::close);
    }

    /**
     * Loads a campaign, applies a change through the aggregate and saves it.
     * A missing campaign is {@code notFound} (404) and a finished one is a {@code conflict} (409).
     *
     * @param cropCampaignId the campaign id
     * @param change         the change to apply through a method of the aggregate
     * @return the id of the campaign, or the error that stopped it
     */
    private Result<Long, ApplicationError> changeCampaignInProgress(Long cropCampaignId,
                                                                   Consumer<CropCampaign> change) {
        var cropCampaign = cropCampaignRepository.findById(cropCampaignId);
        if (cropCampaign.isEmpty()) {
            return Result.failure(ApplicationError.notFound(CROP_CAMPAIGN, cropCampaignId.toString()));
        }
        if (cropCampaign.get().isFinished()) {
            return Result.failure(ApplicationError.conflict(CROP_CAMPAIGN, CAMPAIGN_FINISHED_REASON));
        }
        change.accept(cropCampaign.get());
        cropCampaignRepository.save(cropCampaign.get());
        return Result.success(cropCampaignId);
    }
}
