package com.dymbia.sumaqagro.platform.crop.domain.repositories;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.ClimateForecast;

import java.util.List;

/**
 * Domain repository contract for climate forecasts.
 */
public interface ClimateForecastRepository {

    ClimateForecast save(ClimateForecast forecast);

    List<ClimateForecast> findAllByRegion(String region);
}