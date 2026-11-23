package com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.monitoring;

import java.time.Instant;
import java.util.List;

/**
 * Outbound port to the weather provider.
 * <p>
 * Crop only knows this contract; the AgroMonitoring adapter of the infrastructure implements it.
 * </p>
 *
 * @since 1.1
 */
public interface WeatherForecasting {

    /**
     * Reads the weather forecast of a monitored polygon.
     *
     * @param polygonId the monitoring polygon of the plot
     * @return the forecast points ordered by time, or an empty list when the provider has none
     * @throws Unavailable when the provider cannot be reached or answers with an error
     */
    List<ForecastPoint> findForecast(String polygonId);

    /**
     * One point of the forecast.
     *
     * @param at                 the forecast time
     * @param temperatureCelsius the expected temperature, in Celsius
     * @param rainMillimeters    the expected rain of the interval, in millimeters (0 when no rain)
     */
    record ForecastPoint(Instant at, double temperatureCelsius, double rainMillimeters) {
    }

    final class Unavailable extends RuntimeException {
        public Unavailable() {
            super("Could not read the weather forecast from the monitoring provider");
        }
    }
}
