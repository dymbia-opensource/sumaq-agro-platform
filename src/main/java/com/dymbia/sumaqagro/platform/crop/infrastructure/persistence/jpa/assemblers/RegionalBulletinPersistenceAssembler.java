package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.assemblers;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.RegionalBulletin;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.entities.RegionalBulletinPersistenceEntity;

/**
 * Converts between regional bulletin domain and persistence representations.
 */
public final class RegionalBulletinPersistenceAssembler {

    private RegionalBulletinPersistenceAssembler() {
    }

    /**
     * Reconstructs a domain aggregate from a persisted entity.
     */
    public static RegionalBulletin toDomainFromPersistence(RegionalBulletinPersistenceEntity entity) {
        if (entity == null) {
            return null;
        }

        return RegionalBulletin.reconstitute(
                entity.getId(),
                entity.getAgronomistId(),
                entity.getRegion(),
                entity.getTitle(),
                entity.getContent(),
                entity.getPublishedAt()
        );
    }

    /**
     * Converts a domain aggregate into a JPA entity.
     */
    public static RegionalBulletinPersistenceEntity toPersistenceFromDomain(RegionalBulletin bulletin) {
        if (bulletin == null) {
            return null;
        }

        var entity = new RegionalBulletinPersistenceEntity();

        if (bulletin.getId() != null) {
            entity.setId(bulletin.getId());
        }

        entity.setAgronomistId(bulletin.getAgronomistId());
        entity.setRegion(bulletin.getRegion());
        entity.setTitle(bulletin.getTitle());
        entity.setContent(bulletin.getContent());
        entity.setPublishedAt(bulletin.getPublishedAt());

        return entity;
    }
}
