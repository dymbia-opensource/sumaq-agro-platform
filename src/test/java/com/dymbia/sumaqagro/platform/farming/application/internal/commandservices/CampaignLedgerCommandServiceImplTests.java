package com.dymbia.sumaqagro.platform.farming.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.CampaignLedger;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.FreezeCampaignLedgerCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.OpenCampaignLedgerCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RecordDailyLaborExpenseCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RecordInputExpenseCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CropCampaignId;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.ExpenseCategory;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.Money;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.YieldUnit;
import com.dymbia.sumaqagro.platform.farming.domain.repositories.CampaignLedgerRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CampaignLedgerCommandServiceImplTests {

    private final CampaignLedgerRepository campaignLedgerRepository = mock(CampaignLedgerRepository.class);
    private final CampaignLedgerCommandServiceImpl service =
            new CampaignLedgerCommandServiceImpl(campaignLedgerRepository);

    private static CampaignLedger storedLedger(boolean frozen) {
        return CampaignLedger.reconstitute(3L, new CropCampaignId(7L), YieldUnit.QUINTAL, BigDecimal.ZERO, null,
                frozen, Money.DEFAULT_CURRENCY, List.of());
    }

    private static ApplicationError errorOf(Result<Long, ApplicationError> result) {
        return assertInstanceOf(Result.Failure.class, result).error() instanceof ApplicationError error ? error : null;
    }

    @Test
    void openingASecondLedgerForTheSameCampaignIsAConflictWithItsMessageKey() {
        when(campaignLedgerRepository.existsByCropCampaignId(new CropCampaignId(7L))).thenReturn(true);

        var result = service.handle(new OpenCampaignLedgerCommand(7L, YieldUnit.QUINTAL));

        assertEquals("CAMPAIGNLEDGER_CONFLICT", errorOf(result).code());
        assertEquals("campaign.ledger.error.alreadyOpened", errorOf(result).details());
        verify(campaignLedgerRepository, never()).save(any());
    }

    @Test
    void recordingAnExpenseOnAMissingLedgerIsNotFound() {
        when(campaignLedgerRepository.findById(3L)).thenReturn(Optional.empty());

        var result = service.handle(new RecordInputExpenseCommand(3L, "Urea", BigDecimal.TEN, BigDecimal.ONE,
                LocalDate.now(), null));

        assertEquals("CAMPAIGNLEDGER_NOT_FOUND", errorOf(result).code());
    }

    @Test
    void recordingAnExpenseOnAFrozenLedgerIsAConflictWithTheFrozenMessageKey() {
        when(campaignLedgerRepository.findById(3L)).thenReturn(Optional.of(storedLedger(true)));

        var result = service.handle(new RecordInputExpenseCommand(3L, "Urea", BigDecimal.TEN, BigDecimal.ONE,
                LocalDate.now(), null));

        assertEquals("campaign.ledger.error.frozen", errorOf(result).details());
        verify(campaignLedgerRepository, never()).save(any());
    }

    @Test
    void recordingDailyLaborSavesTheLedgerWithWorkersByDaysAsQuantity() {
        when(campaignLedgerRepository.findById(3L)).thenReturn(Optional.of(storedLedger(false)));

        var result = service.handle(new RecordDailyLaborExpenseCommand(3L, "Aporque", 4, 2, new BigDecimal("50"),
                LocalDate.now(), null));

        var saved = ArgumentCaptor.forClass(CampaignLedger.class);
        verify(campaignLedgerRepository).save(saved.capture());
        var entry = saved.getValue().getEntries().getFirst();
        assertEquals(3L, assertInstanceOf(Result.Success.class, result).value());
        assertEquals(ExpenseCategory.LABOR, entry.getCategory());
        assertEquals(0, new BigDecimal("8").compareTo(entry.getQuantity()));
    }

    @Test
    void freezingAnAlreadyFrozenLedgerSucceedsWithoutSaving() {
        when(campaignLedgerRepository.findByCropCampaignId(new CropCampaignId(7L)))
                .thenReturn(Optional.of(storedLedger(true)));

        var result = service.handle(new FreezeCampaignLedgerCommand(7L));

        assertEquals(3L, assertInstanceOf(Result.Success.class, result).value());
        verify(campaignLedgerRepository, never()).save(any());
    }
}
