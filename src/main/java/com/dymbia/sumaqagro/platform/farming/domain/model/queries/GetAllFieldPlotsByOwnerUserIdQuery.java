package com.dymbia.sumaqagro.platform.farming.domain.model.queries;

public record GetAllFieldPlotsByOwnerUserIdQuery(Long ownerUserId) {
    public GetAllFieldPlotsByOwnerUserIdQuery {
        if (ownerUserId == null || ownerUserId <= 0) {
            throw new IllegalArgumentException("field.plot.error.ownerUserId.invalid");
        }
    }
}
