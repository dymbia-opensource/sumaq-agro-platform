package com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.FieldPlot;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.FieldPlotResource;

import java.util.Objects;

/**
 * Converts the FieldPlot aggregate into its resource.
 */
public final class FieldPlotResourceFromEntityAssembler {

    private FieldPlotResourceFromEntityAssembler() {
    }

    public static FieldPlotResource toResourceFromEntity(FieldPlot entity) {
        Objects.requireNonNull(entity, "Field plot is required.");
        return new FieldPlotResource(
                entity.getId(),
                entity.getOwnerUserId(),
                entity.getName(),
                entity.getRegion(),
                entity.getStatus().name(),
                entity.getAreaHectares(),
                entity.getAgroMonitoringPolygonId());
    }
}