package com.dymbia.sumaqagro.platform.crop.application.internal.queryservices;

import com.dymbia.sumaqagro.platform.crop.application.queryservices.FieldInspectionQueryService;
import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.FieldInspection;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetAllFieldInspectionsByPlotIdQuery;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetFieldInspectionByIdQuery;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.FieldInspectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Application service that resolves field inspection read queries.
 */
@Service
public class FieldInspectionQueryServiceImpl implements FieldInspectionQueryService {

    private final FieldInspectionRepository inspectionRepository;

    public FieldInspectionQueryServiceImpl(FieldInspectionRepository inspectionRepository) {
        this.inspectionRepository = inspectionRepository;
    }

    @Override
    public Optional<FieldInspection> handle(GetFieldInspectionByIdQuery query) {
        return inspectionRepository.findById(
                query.fieldInspectionId()
        );
    }

    @Override
    public List<FieldInspection> handle(GetAllFieldInspectionsByPlotIdQuery query) {
        return inspectionRepository.findAllByPlotId(
                query.plotId()
        );
    }
}