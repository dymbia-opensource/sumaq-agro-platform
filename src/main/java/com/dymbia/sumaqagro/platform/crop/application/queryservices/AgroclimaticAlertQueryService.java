package com.dymbia.sumaqagro.platform.crop.application.queryservices;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.AgroclimaticAlert;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetAgroclimaticAlertsByRegionQuery;

import java.util.List;

/**
 * Application service contract for agroclimatic alert queries.
 */
public interface AgroclimaticAlertQueryService {

    /**
     * Retrieves agroclimatic alerts for a specific region.
     *
     * @param query region identifier
     * @return list of alerts for the region, or an empty list
     */
    List<AgroclimaticAlert> handle(GetAgroclimaticAlertsByRegionQuery query);
}
