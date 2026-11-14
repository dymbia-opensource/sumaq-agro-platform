package com.dymbia.sumaqagro.platform.crop.application.internal.queryservices;

import com.dymbia.sumaqagro.platform.crop.application.queryservices.RegionalBulletinQueryService;
import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.RegionalBulletin;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetRegionalBulletinsByRegionQuery;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.RegionalBulletinRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Retrieves published bulletins for a region.
 */
@Service
public class RegionalBulletinQueryServiceImpl implements RegionalBulletinQueryService {

    private final RegionalBulletinRepository bulletinRepository;

    public RegionalBulletinQueryServiceImpl(RegionalBulletinRepository bulletinRepository) {
        this.bulletinRepository = bulletinRepository;
    }

    @Override
    public List<RegionalBulletin> handle(GetRegionalBulletinsByRegionQuery query) {
        return bulletinRepository.findAllByRegion(query.region());
    }
}
