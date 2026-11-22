package com.dymbia.sumaqagro.platform.crop.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.crop.application.commandservices.AgroclimaticAlertCommandService;
import com.dymbia.sumaqagro.platform.crop.application.commandservices.ClimateForecastCommandService;
import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.acl.ExternalFarmingService;
import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.monitoring.WeatherForecasting;
import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.monitoring.WeatherForecasting.ForecastPoint;
import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.ClimateForecast;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.ImportClimateForecastCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.IssueAgroclimaticAlertCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.RecordClimateForecastCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.AgroclimaticAlertSeverity;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.Percentage;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.ClimateForecastRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class ClimateForecastCommandServiceImpl implements ClimateForecastCommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClimateForecastCommandServiceImpl.class);

    static final String SOURCE = "AgroMonitoring";
    // The forecast days are cut with the time of Peru, where the plots are
    static final ZoneId PLOTS_ZONE = ZoneId.of("America/Lima");
    // Alert thresholds; the team can tune them with the agronomist
    static final double FROST_TEMPERATURE_CELSIUS = 0.0;
    static final double SEVERE_FROST_TEMPERATURE_CELSIUS = -3.0;
    static final double WATER_STRESS_TEMPERATURE_CELSIUS = 30.0;
    static final double WATER_STRESS_MAX_RAIN_MILLIMETERS = 1.0;
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM");

    private final ClimateForecastRepository forecastRepository;
    private final AgroclimaticAlertCommandService alertCommandService;
    private final ExternalFarmingService externalFarmingService;
    private final WeatherForecasting weatherForecasting;
    private final Clock clock;

    @Autowired
    public ClimateForecastCommandServiceImpl(ClimateForecastRepository forecastRepository,
                                             AgroclimaticAlertCommandService alertCommandService,
                                             ExternalFarmingService externalFarmingService,
                                             WeatherForecasting weatherForecasting) {
        this(forecastRepository, alertCommandService, externalFarmingService, weatherForecasting,
                Clock.systemUTC());
    }

    ClimateForecastCommandServiceImpl(ClimateForecastRepository forecastRepository,
                                      AgroclimaticAlertCommandService alertCommandService,
                                      ExternalFarmingService externalFarmingService,
                                      WeatherForecasting weatherForecasting, Clock clock) {
        this.forecastRepository = forecastRepository;
        this.alertCommandService = alertCommandService;
        this.externalFarmingService = externalFarmingService;
        this.weatherForecasting = weatherForecasting;
        this.clock = clock;
    }

    @Override
    public Result<Long, ApplicationError> handle(RecordClimateForecastCommand command) {
        try {
            var forecast = new ClimateForecast(command);
            var savedForecast = forecastRepository.save(forecast);

            return Result.success(savedForecast.getId());
        } catch (RuntimeException e) {
            LOGGER.error("Failed to record climate forecast", e);
            return Result.failure(ApplicationError.unexpected(
                    "record-climate-forecast",
                    "Could not record the climate forecast"
            ));
        }
    }

    @Override
    public Result<List<ClimateForecast>, ApplicationError> handle(ImportClimateForecastCommand command) {
        var plotId = command.plotId();
        // Another producer's plot answers 404, so it does not reveal that the plot exists
        var ownedByRequester = externalFarmingService.fetchOwnerUserIdByPlotId(plotId)
                .filter(ownerUserId -> ownerUserId.equals(command.requestedByUserId()));
        if (ownedByRequester.isEmpty()) {
            return Result.failure(ApplicationError.notFound("FieldPlot", plotId.toString()));
        }
        var polygonId = externalFarmingService.fetchMonitoringPolygonIdByPlotId(plotId);
        var region = externalFarmingService.fetchRegionByPlotId(plotId);
        if (polygonId.isEmpty() || region.isEmpty()) {
            return Result.failure(ApplicationError.conflict(
                    "FieldPlot", "The plot must be linked to a monitoring polygon"));
        }

        List<ForecastPoint> points;
        try {
            points = weatherForecasting.findForecast(polygonId.get());
        } catch (WeatherForecasting.Unavailable exception) {
            return Result.failure(ApplicationError.unexpected(
                    "import-climate-forecast", "Could not read the weather forecast from AgroMonitoring"));
        }
        if (points.isEmpty()) {
            return Result.failure(ApplicationError.notFound("ClimateForecast", plotId.toString()));
        }

        try {
            var forecasts = recordDailyForecasts(region.get(), points);
            issueAlerts(region.get(), points);
            return Result.success(forecasts);
        } catch (RuntimeException exception) {
            LOGGER.error("Failed to import the climate forecast of plot {}", plotId, exception);
            return Result.failure(ApplicationError.unexpected(
                    "import-climate-forecast", "Could not record the imported climate forecast"));
        }
    }

    // One forecast per day: the average temperature, and as rain chance the share of 3-hour intervals with rain
    private List<ClimateForecast> recordDailyForecasts(String region, List<ForecastPoint> points) {
        Map<LocalDate, List<ForecastPoint>> pointsByDay = points.stream().collect(Collectors.groupingBy(
                point -> point.at().atZone(PLOTS_ZONE).toLocalDate(), TreeMap::new, Collectors.toList()));
        var forecasts = new ArrayList<ClimateForecast>();
        pointsByDay.forEach((day, dayPoints) -> {
            var averageTemperature = dayPoints.stream().mapToDouble(ForecastPoint::temperatureCelsius)
                    .average().orElseThrow();
            var rainyIntervals = dayPoints.stream().filter(point -> point.rainMillimeters() > 0).count();
            var rainChance = 100.0 * rainyIntervals / dayPoints.size();
            var command = new RecordClimateForecastCommand(region, round(averageTemperature),
                    new Percentage(round(rainChance)), day.atStartOfDay(PLOTS_ZONE).toInstant(), SOURCE);
            forecasts.add(forecastRepository.save(new ClimateForecast(command)));
        });
        return forecasts;
    }

    // At most one alert of each kind per import, from the 3-hour points so a cold night is not averaged away
    private void issueAlerts(String region, List<ForecastPoint> points) {
        var coldest = points.stream().min(Comparator.comparingDouble(ForecastPoint::temperatureCelsius))
                .orElseThrow();
        if (coldest.temperatureCelsius() <= FROST_TEMPERATURE_CELSIUS) {
            var severity = coldest.temperatureCelsius() <= SEVERE_FROST_TEMPERATURE_CELSIUS
                    ? AgroclimaticAlertSeverity.CRITICAL : AgroclimaticAlertSeverity.HIGH;
            issueAlert(region, severity, "Riesgo de helada",
                    "Se pronostica una temperatura mínima de %s °C el %s. Protege los cultivos sensibles al frío."
                            .formatted(round(coldest.temperatureCelsius()), dayOf(coldest)));
        }

        var hottest = points.stream().max(Comparator.comparingDouble(ForecastPoint::temperatureCelsius))
                .orElseThrow();
        var totalRain = points.stream().mapToDouble(ForecastPoint::rainMillimeters).sum();
        if (hottest.temperatureCelsius() >= WATER_STRESS_TEMPERATURE_CELSIUS
                && totalRain < WATER_STRESS_MAX_RAIN_MILLIMETERS) {
            issueAlert(region, AgroclimaticAlertSeverity.HIGH, "Riesgo de estrés hídrico",
                    ("Se pronostica una temperatura máxima de %s °C el %s y menos de %s mm de lluvia en los "
                            + "próximos días. Revisa el riego.")
                            .formatted(round(hottest.temperatureCelsius()), dayOf(hottest),
                                    WATER_STRESS_MAX_RAIN_MILLIMETERS));
        }
    }

    private void issueAlert(String region, AgroclimaticAlertSeverity severity, String title, String description) {
        var result = alertCommandService.handle(new IssueAgroclimaticAlertCommand(
                title, description, severity, region, clock.instant(), SOURCE));
        if (result.isFailure()) {
            LOGGER.warn("The alert '{}' for region {} was not issued: {}", title, region, result);
        }
    }

    private static String dayOf(ForecastPoint point) {
        return point.at().atZone(PLOTS_ZONE).format(DAY_FORMAT);
    }

    private static double round(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }
}
