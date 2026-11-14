package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.assemblers;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.ClimateForecast;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.entities.ClimateForecastPersistenceEntity;

/**
 * Converts between climate forecast domain and persistence representations.
 */
public final class ClimateForecastPersistenceAssembler {

    private ClimateForecastPersistenceAssembler() {
    }

    /**
     * Reconstructs a domain aggregate from a persisted entity.
     */
    public static ClimateForecast toDomainFromPersistence(ClimateForecastPersistenceEntity entity) {
        if (entity == null) return null;

        return ClimateForecast.reconstitute(
                entity.getId(),
                entity.getRegion(),
                entity.getTemperatureCelsius(),
                entity.getRainChance(),
                entity.getForecastDate(),
                entity.getSource()
        );
    }

    /**
     * Converts a domain aggregate into a JPA entity.
     */
    public static ClimateForecastPersistenceEntity toPersistenceFromDomain(ClimateForecast forecast) {
        if (forecast == null) return null;

        var entity = new ClimateForecastPersistenceEntity();

        if (forecast.getId() != null) {
            entity.setId(forecast.getId());
        }

        entity.setRegion(forecast.getRegion());
        entity.setTemperatureCelsius(forecast.getTemperatureCelsius());
        entity.setRainChance(forecast.getRainChance());
        entity.setForecastDate(forecast.getForecastDate());
        entity.setSource(forecast.getSource());

        return entity;
    }
}
