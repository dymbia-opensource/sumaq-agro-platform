package com.dymbia.sumaqagro.platform.farming.application.acl;

import com.dymbia.sumaqagro.platform.farming.application.queryservices.FieldPlotQueryService;
import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.FieldPlot;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetFieldPlotByIdQuery;
import com.dymbia.sumaqagro.platform.farming.interfaces.acl.FarmingContextFacade;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link FarmingContextFacade}. Uses only the query service.
 *
 * @since 1.0
 */
@Service
public class FarmingContextFacadeImpl implements FarmingContextFacade {

    private final FieldPlotQueryService fieldPlotQueryService;

    public FarmingContextFacadeImpl(FieldPlotQueryService fieldPlotQueryService) {
        this.fieldPlotQueryService = fieldPlotQueryService;
    }

    @Override
    public Long fetchOwnerUserIdByFieldPlotId(Long fieldPlotId) {
        if (fieldPlotId == null || fieldPlotId <= 0) return 0L;
        return fieldPlotQueryService.handle(new GetFieldPlotByIdQuery(fieldPlotId))
                .map(FieldPlot::getOwnerUserId)
                .orElse(0L);
    }

    @Override
    public boolean existsFieldPlotById(Long fieldPlotId) {
        return fetchOwnerUserIdByFieldPlotId(fieldPlotId) > 0L;
    }
}
