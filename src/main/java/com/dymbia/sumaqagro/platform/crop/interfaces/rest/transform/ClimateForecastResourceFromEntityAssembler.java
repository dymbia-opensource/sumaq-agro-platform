package com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.ClimateForecast;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.ClimateForecastResource;

/**
 * Converts a climate forecast aggregate into an API response resource.
 */
public class ClimateForecastResourceFromEntityAssembler {

    /**
     * Converts a {@link ClimateForecast} entity into a {@link ClimateForecastResource}.
     *
     * @param forecast The climate forecast entity to convert.
     * @return The resulting resource.
     */
    public static ClimateForecastResource toResourceFromEntity(ClimateForecast forecast) {
        return new ClimateForecastResource(
                forecast.getId(),
                forecast.getRegion(),
                forecast.getTemperatureCelsius(),
                forecast.getRainChance().value(),
                forecast.getForecastDate(),
                forecast.getSource()
        );
    }
}