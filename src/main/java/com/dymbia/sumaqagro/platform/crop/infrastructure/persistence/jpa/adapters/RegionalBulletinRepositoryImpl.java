package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.RegionalBulletin;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.RegionalBulletinRepository;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.assemblers.RegionalBulletinPersistenceAssembler;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.repositories.RegionalBulletinPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Implements the regional bulletin domain repository using Spring Data JPA.
 */
@Repository
public class RegionalBulletinRepositoryImpl implements RegionalBulletinRepository {

    private final RegionalBulletinPersistenceRepository persistenceRepository;

    public RegionalBulletinRepositoryImpl(RegionalBulletinPersistenceRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public RegionalBulletin save(RegionalBulletin bulletin) {
        var entity = RegionalBulletinPersistenceAssembler.toPersistenceFromDomain(bulletin);
        var savedEntity = persistenceRepository.save(entity);

        return RegionalBulletinPersistenceAssembler.toDomainFromPersistence(savedEntity);
    }

    @Override
    public List<RegionalBulletin> findAllByRegion(String region) {
        return persistenceRepository.findAllByRegionOrderByPublishedAtDescIdDesc(region)
                .stream()
                .map(RegionalBulletinPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }
}
