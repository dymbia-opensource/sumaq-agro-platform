package com.dymbia.sumaqagro.platform.farming.domain.model.queries;

public record GetFieldPlotByIdQuery(Long fieldPlotId) {
    public GetFieldPlotByIdQuery {
        if (fieldPlotId == null || fieldPlotId <= 0) {
            throw new IllegalArgumentException("field.plot.error.id.invalid");
        }
    }
}
