package com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.assemblers;

import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.FieldPlot;
import com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.entities.FieldPlotPersistenceEntity;

/**
 * Converts a field plot between the domain aggregate and its JPA entity.
 */
public final class FieldPlotPersistenceAssembler {

    private FieldPlotPersistenceAssembler() {
    }

    /**
     * Builds the domain aggregate from a stored entity.
     *
     * @param entity the entity read from the database
     * @return the field plot with the stored state
     */
    public static FieldPlot toDomainFromPersistence(FieldPlotPersistenceEntity entity) {
        return FieldPlot.reconstitute(
                entity.getId(),
                entity.getOwnerUserId(),
                entity.getName(),
                entity.getRegion(),
                entity.getStatus(),
                entity.getAreaHectares(),
                entity.getAgroMonitoringPolygonId(),
                entity.getBoundary());
    }

    /**
     * Builds the JPA entity from a domain aggregate.
     * If the plot has no id the entity is inserted, otherwise it is updated.
     *
     * @param fieldPlot the domain aggregate
     * @return the entity ready to save
     */
    public static FieldPlotPersistenceEntity toPersistenceFromDomain(FieldPlot fieldPlot) {
        var entity = new FieldPlotPersistenceEntity();
        entity.setId(fieldPlot.getId());
        entity.setOwnerUserId(fieldPlot.getOwnerUserId());
        entity.setName(fieldPlot.getName());
        entity.setRegion(fieldPlot.getRegion());
        entity.setStatus(fieldPlot.getStatus());
        entity.setAreaHectares(fieldPlot.getAreaHectares());
        entity.setAgroMonitoringPolygonId(fieldPlot.getAgroMonitoringPolygonId());
        entity.setBoundary(fieldPlot.getBoundary());
        return entity;
    }
}