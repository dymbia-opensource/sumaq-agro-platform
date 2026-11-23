package com.dymbia.sumaqagro.platform.farming.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.FieldPlot;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.LinkPlotPolygonCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RecordSoilBaselineCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RegisterFieldPlotCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.SoilBaseline;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.SoilTexture;
import com.dymbia.sumaqagro.platform.farming.domain.repositories.FieldPlotRepository;
import com.dymbia.sumaqagro.platform.profiles.interfaces.acl.ProfilesContextFacade;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import com.dymbia.sumaqagro.platform.subscriptions.interfaces.acl.SubscriptionsContextFacade;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FieldPlotCommandServiceImplTests {

    private final FieldPlotRepository fieldPlotRepository = mock(FieldPlotRepository.class);
    private final FieldPlotCommandServiceImpl service = new FieldPlotCommandServiceImpl(fieldPlotRepository,
            mock(SubscriptionsContextFacade.class), mock(ProfilesContextFacade.class), mock(TransactionTemplate.class));

    private static FieldPlot registeredPlot() {
        return new FieldPlot(new RegisterFieldPlotCommand(1L, "Parcela Apurímac", "Apurímac", BigDecimal.ONE));
    }

    private static ApplicationError errorOf(Result<Long, ApplicationError> result) {
        return assertInstanceOf(Result.Failure.class, result).error() instanceof ApplicationError error ? error : null;
    }

    @Test
    void changingAMissingPlotIsNotFound() {
        when(fieldPlotRepository.findById(1L)).thenReturn(Optional.empty());

        var result = service.handle(new LinkPlotPolygonCommand(1L, "polygon-1"));

        assertEquals("FIELDPLOT_NOT_FOUND", errorOf(result).code());
    }

    @Test
    void linkingAPolygonBeforeTheBoundaryIsAConflictInsteadOfAnInternalError() {
        when(fieldPlotRepository.findById(1L)).thenReturn(Optional.of(registeredPlot()));

        var result = service.handle(new LinkPlotPolygonCommand(1L, "polygon-1"));

        var error = errorOf(result);
        assertEquals("FIELDPLOT_CONFLICT", error.code());
        assertEquals("field.plot.error.boundary.required", error.details());
        verify(fieldPlotRepository, never()).save(any());
    }

    @Test
    void recordingTheSoilBaselineSavesThePlot() {
        var plot = registeredPlot();
        when(fieldPlotRepository.findById(1L)).thenReturn(Optional.of(plot));
        var soilBaseline = new SoilBaseline(new BigDecimal("6.5"), SoilTexture.LOAMY, new BigDecimal("3.2"),
                LocalDate.of(2026, 10, 1));

        var result = service.handle(new RecordSoilBaselineCommand(1L, soilBaseline));

        assertEquals(1L, assertInstanceOf(Result.Success.class, result).value());
        assertEquals(soilBaseline, plot.getSoilBaseline());
        verify(fieldPlotRepository).save(plot);
    }
}
