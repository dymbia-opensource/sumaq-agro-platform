package com.dymbia.sumaqagro.platform.crop.application.queryservices;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.ClimateForecast;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetClimateForecastsByRegionQuery;

import java.util.List;

/**
 * Application service contract for climate forecast queries.
 */
public interface ClimateForecastQueryService {

    /**
     * Retrieves climate forecasts for a specific region.
     *
     * @param query region identifier
     * @return list of climate forecasts for the region, or an empty list
     */
    List<ClimateForecast> handle(GetClimateForecastsByRegionQuery query);
}