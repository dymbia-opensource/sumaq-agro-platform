package com.dymbia.sumaqagro.platform.farming.application.internal.queryservices;

import com.dymbia.sumaqagro.platform.farming.application.queryservices.FieldPlotQueryService;
import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.FieldPlot;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetAllFieldPlotsByOwnerUserIdQuery;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetFieldPlotByIdQuery;
import com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.FieldPlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class FieldPlotQueryServiceImpl implements FieldPlotQueryService {
    private final FieldPlotRepository fieldPlotRepository;

    public FieldPlotQueryServiceImpl(FieldPlotRepository fieldPlotRepository) {
        this.fieldPlotRepository = fieldPlotRepository;
    }
    @Override
    public Optional<FieldPlot> handle(GetFieldPlotByIdQuery query) {
        return fieldPlotRepository.findById(query.fieldPlotId());
    }

    @Override
    public List<FieldPlot> handle(GetAllFieldPlotsByOwnerUserIdQuery query) {
        return fieldPlotRepository.findAllByOwnerUserId(query.ownerUserId());
    }

}
