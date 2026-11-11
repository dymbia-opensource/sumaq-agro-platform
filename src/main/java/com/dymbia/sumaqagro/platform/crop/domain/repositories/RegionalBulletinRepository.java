package com.dymbia.sumaqagro.platform.crop.domain.repositories;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.RegionalBulletin;

import java.util.List;

/**
 * Domain repository contract for regional bulletins.
 */
public interface RegionalBulletinRepository {

    RegionalBulletin save(RegionalBulletin bulletin);

    List<RegionalBulletin> findAllByRegion(String region);
}
