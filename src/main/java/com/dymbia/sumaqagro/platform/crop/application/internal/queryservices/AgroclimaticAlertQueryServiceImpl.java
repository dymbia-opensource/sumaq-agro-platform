package com.dymbia.sumaqagro.platform.crop.application.internal.queryservices;

import com.dymbia.sumaqagro.platform.crop.application.queryservices.AgroclimaticAlertQueryService;
import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.AgroclimaticAlert;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetAgroclimaticAlertsByRegionQuery;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.AgroclimaticAlertRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Retrieves agroclimatic alerts issued for a region.
 */
@Service
public class AgroclimaticAlertQueryServiceImpl implements AgroclimaticAlertQueryService {

    private final AgroclimaticAlertRepository alertRepository;

    public AgroclimaticAlertQueryServiceImpl(AgroclimaticAlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Override
    public List<AgroclimaticAlert> handle(GetAgroclimaticAlertsByRegionQuery query) {
        return alertRepository.findAllByRegion(query.region());
    }
}
