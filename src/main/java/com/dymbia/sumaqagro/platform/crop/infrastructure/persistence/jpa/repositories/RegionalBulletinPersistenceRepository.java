package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.entities.RegionalBulletinPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for regional bulletins.
 */
public interface RegionalBulletinPersistenceRepository extends JpaRepository<RegionalBulletinPersistenceEntity, Long> {

    /**
     * Finds all regional bulletin persistence entities by region, ordered by published date and ID in descending order.
     *
     * @param region the region to filter by
     * @return a list of regional bulletin persistence entities associated with the given region
     */
    List<RegionalBulletinPersistenceEntity> findAllByRegionOrderByPublishedAtDescIdDesc(String region);
}
