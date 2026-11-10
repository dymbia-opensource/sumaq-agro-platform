package com.dymbia.sumaqagro.platform.crop.application.internal.queryservices;

import com.dymbia.sumaqagro.platform.crop.application.queryservices.TechnicalPrescriptionQueryService;
import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.TechnicalPrescription;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetTechnicalPrescriptionByIdQuery;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetTechnicalPrescriptionsByPlotIdQuery;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.TechnicalPrescriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Resolves technical prescription read queries.
 */
@Service
public class TechnicalPrescriptionQueryServiceImpl implements TechnicalPrescriptionQueryService {

    private final TechnicalPrescriptionRepository prescriptionRepository;

    public TechnicalPrescriptionQueryServiceImpl(TechnicalPrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    @Override
    public Optional<TechnicalPrescription> handle(GetTechnicalPrescriptionByIdQuery query) {
        return prescriptionRepository.findById(query.technicalPrescriptionId());
    }

    @Override
    public List<TechnicalPrescription> handle(GetTechnicalPrescriptionsByPlotIdQuery query) {
        return prescriptionRepository.findAllByPlotId(query.plotId());
    }
}