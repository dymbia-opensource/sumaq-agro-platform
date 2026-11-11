package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.assemblers;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.AgroclimaticAlert;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.entities.AgroclimaticAlertPersistenceEntity;

/**
 * Converts between agroclimatic alert domain and persistence representations.
 */
public final class AgroclimaticAlertPersistenceAssembler {

    private AgroclimaticAlertPersistenceAssembler() {
    }

    /**
     * Reconstructs a domain aggregate from a persisted entity.
     */
    public static AgroclimaticAlert toDomainFromPersistence(AgroclimaticAlertPersistenceEntity entity) {
        if (entity == null) return null;

        return AgroclimaticAlert.reconstitute(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getSeverity(),
                entity.getRegion(),
                entity.getIssuedAt(),
                entity.getSource()
        );
    }

    /**
     * Converts a domain aggregate to a persistence entity.
     */
    public static AgroclimaticAlertPersistenceEntity toPersistenceFromDomain(AgroclimaticAlert alert) {
        if (alert == null) return null;

        var entity = new AgroclimaticAlertPersistenceEntity();

        if (alert.getId() != null) {
            entity.setId(alert.getId());
        }

        entity.setTitle(alert.getTitle());
        entity.setDescription(alert.getDescription());
        entity.setSeverity(alert.getSeverity());
        entity.setRegion(alert.getRegion());
        entity.setIssuedAt(alert.getIssuedAt());
        entity.setSource(alert.getSource());

        return entity;
    }
}
