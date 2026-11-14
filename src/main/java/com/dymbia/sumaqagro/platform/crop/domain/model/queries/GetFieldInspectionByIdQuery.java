package com.dymbia.sumaqagro.platform.crop.domain.model.queries;

/**
 * Requests a field inspection by its identifier.
 */
public record GetFieldInspectionByIdQuery(Long fieldInspectionId) {

    public GetFieldInspectionByIdQuery {
        if (fieldInspectionId == null || fieldInspectionId <= 0) {
            throw new IllegalArgumentException("Field inspection ID must be greater than zero");
        }
    }
}