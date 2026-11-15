package com.dymbia.sumaqagro.platform.farming.application.queryservices;

import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.FieldPlot;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetAllFieldPlotsByOwnerUserIdQuery;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetFieldPlotByIdQuery;

import java.util.List;
import java.util.Optional;

public interface FieldPlotQueryService {
    Optional<FieldPlot> handle(GetFieldPlotByIdQuery query);
    List<FieldPlot> handle (GetAllFieldPlotsByOwnerUserIdQuery query);
}
