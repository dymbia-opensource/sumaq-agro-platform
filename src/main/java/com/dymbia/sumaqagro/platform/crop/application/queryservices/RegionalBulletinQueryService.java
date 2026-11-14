package com.dymbia.sumaqagro.platform.crop.application.queryservices;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.RegionalBulletin;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetRegionalBulletinsByRegionQuery;

import java.util.List;

/**
 * Application service contract for querying regional bulletins.
 */
public interface RegionalBulletinQueryService {

    /**
     * Handles the retrieval of regional bulletins by region.
     *
     * @param query query containing region data
     * @return list of regional bulletins for the specified region
     * @see GetRegionalBulletinsByRegionQuery
     */
    List<RegionalBulletin> handle(GetRegionalBulletinsByRegionQuery query);
}
