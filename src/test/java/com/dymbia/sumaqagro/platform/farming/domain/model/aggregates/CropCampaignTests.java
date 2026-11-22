package com.dymbia.sumaqagro.platform.farming.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.StartCropCampaignCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.events.CropCampaignClosedEvent;
import com.dymbia.sumaqagro.platform.farming.domain.model.events.CropCampaignStartedEvent;
import com.dymbia.sumaqagro.platform.farming.domain.model.events.SowingDateRecordedEvent;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CampaignStatus;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CropType;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.FieldPlotId;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.SeedVariety;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CropCampaignTests {

    private static CropCampaign startedCampaign(LocalDate sowingDate) {
        return new CropCampaign(new StartCropCampaignCommand(1L, "2026-I", CropType.ANDEAN_POTATO,
                new SeedVariety("Yungay"), false, sowingDate));
    }

    @Test
    void newCampaignIsInProgress() {
        var campaign = startedCampaign(null);

        assertEquals(CampaignStatus.IN_PROGRESS, campaign.getStatus());
        assertTrue(campaign.domainEvents().isEmpty());
    }

    @Test
    void sowingDateRaisesTheSowingDateRecordedEvent() {
        var campaign = startedCampaign(LocalDate.of(2026, 10, 15));

        assertInstanceOf(SowingDateRecordedEvent.class, campaign.domainEvents().iterator().next());
    }

    @Test
    void finishedCampaignRejectsChangesWithTheFinishedMessageKey() {
        var campaign = startedCampaign(null);
        campaign.close();

        var exception = assertThrows(IllegalStateException.class,
                () -> campaign.selectCropType(CropType.SPECIALTY_COFFEE));

        assertEquals("crop.campaign.error.finished", exception.getMessage());
    }

    @Test
    void missingStatusIsAnInvalidArgumentWithItsMessageKey() {
        var exception = assertThrows(IllegalArgumentException.class, () -> CropCampaign.reconstitute(1L,
                new FieldPlotId(1L), "2026-I", CropType.ANDEAN_POTATO, new SeedVariety("Yungay"), false, null, null));

        assertEquals("crop.campaign.error.status.required", exception.getMessage());
    }

    @Test
    void missingSowingDateIsAnInvalidArgumentWithItsMessageKey() {
        var campaign = startedCampaign(null);

        var exception = assertThrows(IllegalArgumentException.class, () -> campaign.recordSowingDate(null));

        assertEquals("crop.campaign.error.sowingDate.required", exception.getMessage());
    }

    @Test
    void startingRaisesTheCropCampaignStartedEventWithItsCropType() {
        var campaign = CropCampaign.reconstitute(7L, new FieldPlotId(1L), "2026-I", CropType.SPECIALTY_COFFEE,
                new SeedVariety("Caturra"), false, null, CampaignStatus.IN_PROGRESS);

        campaign.onStarted();

        var event = assertInstanceOf(CropCampaignStartedEvent.class, campaign.domainEvents().iterator().next());
        assertEquals(7L, event.cropCampaignId());
        assertEquals(CropType.SPECIALTY_COFFEE, event.cropType());
    }

    @Test
    void closingRaisesTheCropCampaignClosedEvent() {
        var campaign = CropCampaign.reconstitute(7L, new FieldPlotId(1L), "2026-I", CropType.ANDEAN_POTATO,
                new SeedVariety("Yungay"), false, null, CampaignStatus.IN_PROGRESS);

        campaign.close();

        var event = assertInstanceOf(CropCampaignClosedEvent.class, campaign.domainEvents().iterator().next());
        assertEquals(7L, event.cropCampaignId());
    }
}
