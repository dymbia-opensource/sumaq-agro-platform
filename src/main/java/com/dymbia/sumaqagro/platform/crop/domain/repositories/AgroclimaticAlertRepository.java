package com.dymbia.sumaqagro.platform.crop.domain.repositories;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.AgroclimaticAlert;

import java.util.List;

/**
 * Domain repository contract for agroclimatic alerts.
 */
public interface AgroclimaticAlertRepository {

    AgroclimaticAlert save(AgroclimaticAlert alert);

    List<AgroclimaticAlert> findAllByRegion(String region);
}