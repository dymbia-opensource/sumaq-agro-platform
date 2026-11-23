package com.dymbia.sumaqagro.platform.crop.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.crop.application.commandservices.AgroclimaticAlertCommandService;
import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.acl.ExternalFarmingService;
import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.monitoring.WeatherForecasting;
import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.monitoring.WeatherForecasting.ForecastPoint;
import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.ClimateForecast;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.ImportClimateForecastCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.IssueAgroclimaticAlertCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.AgroclimaticAlertSeverity;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.ClimateForecastRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClimateForecastCommandServiceImplTests {

    private static final Long PLOT_ID = 10L;
    private static final Long OWNER_ID = 5L;
    // 2026-11-23 06:00 in Lima (UTC-5)
    private static final Instant MORNING = Instant.parse("2026-11-23T11:00:00Z");

    private final ClimateForecastRepository forecastRepository = mock(ClimateForecastRepository.class);
    private final AgroclimaticAlertCommandService alertCommandService = mock(AgroclimaticAlertCommandService.class);
    private final ExternalFarmingService externalFarmingService = mock(ExternalFarmingService.class);
    private final WeatherForecasting weatherForecasting = mock(WeatherForecasting.class);
    private final ClimateForecastCommandServiceImpl service = new ClimateForecastCommandServiceImpl(
            forecastRepository, alertCommandService, externalFarmingService, weatherForecasting,
            Clock.fixed(MORNING, ZoneOffset.UTC));

    @BeforeEach
    void plotWithPolygon() {
        when(externalFarmingService.fetchOwnerUserIdByPlotId(PLOT_ID)).thenReturn(Optional.of(OWNER_ID));
        when(externalFarmingService.fetchMonitoringPolygonIdByPlotId(PLOT_ID)).thenReturn(Optional.of("poly-1"));
        when(externalFarmingService.fetchRegionByPlotId(PLOT_ID)).thenReturn(Optional.of("Junín"));
        when(forecastRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(alertCommandService.handle(any(IssueAgroclimaticAlertCommand.class))).thenReturn(Result.success(1L));
    }

    private static ForecastPoint point(int hoursAfterMorning, double celsius, double rainMillimeters) {
        return new ForecastPoint(MORNING.plusSeconds(hoursAfterMorning * 3600L), celsius, rainMillimeters);
    }

    @SuppressWarnings("unchecked")
    private static List<ClimateForecast> forecastsOf(Result<List<ClimateForecast>, ApplicationError> result) {
        return (List<ClimateForecast>) assertInstanceOf(Result.Success.class, result).value();
    }

    private List<IssueAgroclimaticAlertCommand> issuedAlerts(int expected) {
        var captor = ArgumentCaptor.forClass(IssueAgroclimaticAlertCommand.class);
        verify(alertCommandService, org.mockito.Mockito.times(expected)).handle(captor.capture());
        return captor.getAllValues();
    }

    @Test
    void anotherProducersPlotIsNotFoundAndTheProviderIsNotCalled() {
        var result = service.handle(new ImportClimateForecastCommand(PLOT_ID, 99L));

        var error = (ApplicationError) assertInstanceOf(Result.Failure.class, result).error();
        assertEquals("FIELDPLOT_NOT_FOUND", error.code());
        verify(weatherForecasting, never()).findForecast(any());
    }

    @Test
    void theForecastIsRecordedAsOneAveragePerDayWithTheShareOfRainyIntervals() {
        // Day 1 (Lima): 4 points, 1 with rain. Day 2: 2 points, both dry
        when(weatherForecasting.findForecast("poly-1")).thenReturn(List.of(
                point(0, 10, 0), point(3, 14, 2.5), point(6, 18, 0), point(9, 14, 0),
                point(24, 12, 0), point(27, 16, 0)));

        var forecasts = forecastsOf(service.handle(new ImportClimateForecastCommand(PLOT_ID, OWNER_ID)));

        assertEquals(2, forecasts.size());
        assertEquals(14.0, forecasts.get(0).getTemperatureCelsius());
        assertEquals(25.0, forecasts.get(0).getRainChance().value());
        assertEquals("Junín", forecasts.get(0).getRegion());
        assertEquals(Instant.parse("2026-11-23T05:00:00Z"), forecasts.get(0).getForecastDate());
        assertEquals(0.0, forecasts.get(1).getRainChance().value());
        issuedAlerts(0);
    }

    @Test
    void aColdNightIssuesAFrostAlertEvenWhenTheDailyAverageIsWarm() {
        when(weatherForecasting.findForecast("poly-1")).thenReturn(List.of(
                point(0, 15, 0), point(3, -1.2, 0), point(6, 20, 0)));

        service.handle(new ImportClimateForecastCommand(PLOT_ID, OWNER_ID));

        var alert = issuedAlerts(1).getFirst();
        assertEquals("Riesgo de helada", alert.title());
        assertEquals(AgroclimaticAlertSeverity.HIGH, alert.severity());
        assertEquals("Junín", alert.region());
        assertEquals(MORNING, alert.issuedAt());
    }

    @Test
    void aSevereFrostIsCritical() {
        when(weatherForecasting.findForecast("poly-1")).thenReturn(List.of(point(0, -4.5, 0)));

        service.handle(new ImportClimateForecastCommand(PLOT_ID, OWNER_ID));

        assertEquals(AgroclimaticAlertSeverity.CRITICAL, issuedAlerts(1).getFirst().severity());
    }

    @Test
    void heatWithoutRainIssuesAWaterStressAlert() {
        when(weatherForecasting.findForecast("poly-1")).thenReturn(List.of(point(0, 22, 0), point(3, 31, 0.4)));

        service.handle(new ImportClimateForecastCommand(PLOT_ID, OWNER_ID));

        assertEquals("Riesgo de estrés hídrico", issuedAlerts(1).getFirst().title());
    }

    @Test
    void heatWithEnoughRainIssuesNoAlert() {
        when(weatherForecasting.findForecast("poly-1")).thenReturn(List.of(point(0, 31, 3.0)));

        service.handle(new ImportClimateForecastCommand(PLOT_ID, OWNER_ID));

        issuedAlerts(0);
    }

    @Test
    void anUnavailableProviderIsAnUnexpectedErrorAndNothingIsRecorded() {
        when(weatherForecasting.findForecast("poly-1")).thenThrow(new WeatherForecasting.Unavailable());

        var result = service.handle(new ImportClimateForecastCommand(PLOT_ID, OWNER_ID));

        assertInstanceOf(Result.Failure.class, result);
        verify(forecastRepository, never()).save(any());
    }
}
