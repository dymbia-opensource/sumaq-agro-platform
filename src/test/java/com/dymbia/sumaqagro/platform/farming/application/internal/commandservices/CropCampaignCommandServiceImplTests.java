package com.dymbia.sumaqagro.platform.farming.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.CropCampaign;
import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.FieldPlot;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.CloseCropCampaignCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.StartCropCampaignCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.UpdateCropCampaignCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CampaignStatus;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CropType;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.FieldPlotId;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.SeedVariety;
import com.dymbia.sumaqagro.platform.farming.domain.repositories.CropCampaignRepository;
import com.dymbia.sumaqagro.platform.farming.domain.repositories.FieldPlotRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CropCampaignCommandServiceImplTests {

    private final CropCampaignRepository cropCampaignRepository = mock(CropCampaignRepository.class);
    private final FieldPlotRepository fieldPlotRepository = mock(FieldPlotRepository.class);
    private final CropCampaignCommandServiceImpl service =
            new CropCampaignCommandServiceImpl(cropCampaignRepository, fieldPlotRepository);

    private static CropCampaign storedCampaign(CampaignStatus status) {
        return CropCampaign.reconstitute(7L, new FieldPlotId(1L), "2026-I", CropType.ANDEAN_POTATO,
                new SeedVariety("Yungay"), false, null, status);
    }

    private static ApplicationError errorOf(Result<Long, ApplicationError> result) {
        return assertInstanceOf(Result.Failure.class, result).error() instanceof ApplicationError error ? error : null;
    }

    @Test
    void startingOnAMissingPlotIsNotFound() {
        when(fieldPlotRepository.findById(1L)).thenReturn(Optional.empty());

        var result = service.handle(new StartCropCampaignCommand(1L, "2026-I", CropType.ANDEAN_POTATO,
                new SeedVariety("Yungay"), false, null));

        assertEquals("FIELDPLOT_NOT_FOUND", errorOf(result).code());
    }

    @Test
    void startingOnAPlotWithACampaignInProgressIsAConflictWithItsMessageKey() {
        when(fieldPlotRepository.findById(1L)).thenReturn(Optional.of(mock(FieldPlot.class)));
        when(cropCampaignRepository.existsByFieldPlotIdAndStatus(new FieldPlotId(1L), CampaignStatus.IN_PROGRESS))
                .thenReturn(true);

        var result = service.handle(new StartCropCampaignCommand(1L, "2026-I", CropType.ANDEAN_POTATO,
                new SeedVariety("Yungay"), false, null));

        var error = errorOf(result);
        assertEquals("CROPCAMPAIGN_CONFLICT", error.code());
        assertEquals("crop.campaign.error.inProgress", error.details());
    }

    @Test
    void changingAFinishedCampaignIsAConflictAndSavesNothing() {
        when(cropCampaignRepository.findById(7L)).thenReturn(Optional.of(storedCampaign(CampaignStatus.FINISHED)));

        var result = service.handle(new CloseCropCampaignCommand(7L));

        var error = errorOf(result);
        assertEquals("CROPCAMPAIGN_CONFLICT", error.code());
        assertEquals("crop.campaign.error.finished", error.details());
        verify(cropCampaignRepository, never()).save(any());
    }

    @Test
    void updatingACampaignInProgressSavesIt() {
        var campaign = storedCampaign(CampaignStatus.IN_PROGRESS);
        when(cropCampaignRepository.findById(7L)).thenReturn(Optional.of(campaign));

        var result = service.handle(new UpdateCropCampaignCommand(7L, CropType.SPECIALTY_COFFEE, null, false, null));

        assertEquals(7L, assertInstanceOf(Result.Success.class, result).value());
        assertEquals(CropType.SPECIALTY_COFFEE, campaign.getCropType());
        verify(cropCampaignRepository).save(campaign);
    }
}
