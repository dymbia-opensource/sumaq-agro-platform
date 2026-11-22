package com.dymbia.sumaqagro.platform.farming.application.acl;

import com.dymbia.sumaqagro.platform.farming.application.queryservices.FieldPlotQueryService;
import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.FieldPlot;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetFieldPlotByIdQuery;
import com.dymbia.sumaqagro.platform.farming.interfaces.acl.FarmingContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

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
        return findFieldPlot(fieldPlotId).map(FieldPlot::getOwnerUserId).orElse(0L);
    }

    @Override
    public boolean existsFieldPlotById(Long fieldPlotId) {
        return fetchOwnerUserIdByFieldPlotId(fieldPlotId) > 0L;
    }

    @Override
    public String fetchAgroMonitoringPolygonIdByFieldPlotId(Long fieldPlotId) {
        return fetchText(fieldPlotId, FieldPlot::getAgroMonitoringPolygonId);
    }

    @Override
    public String fetchFieldPlotNameById(Long fieldPlotId) {
        return fetchText(fieldPlotId, FieldPlot::getName);
    }

    @Override
    public String fetchFieldPlotRegionById(Long fieldPlotId) {
        return fetchText(fieldPlotId, FieldPlot::getRegion);
    }

    // A missing plot or a missing value is an empty string, so the other contexts never receive null
    private String fetchText(Long fieldPlotId, Function<FieldPlot, String> value) {
        return findFieldPlot(fieldPlotId).map(value).orElse("");
    }

    private Optional<FieldPlot> findFieldPlot(Long fieldPlotId) {
        if (fieldPlotId == null || fieldPlotId <= 0) return Optional.empty();
        return fieldPlotQueryService.handle(new GetFieldPlotByIdQuery(fieldPlotId));
    }
}
