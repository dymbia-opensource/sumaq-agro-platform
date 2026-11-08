package com.dymbia.sumaqagro.platform.certification.infrastructure.acl;

import com.dymbia.sumaqagro.platform.certification.application.acl.ExternalFieldManagementService;
import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.FieldPlot;
import com.dymbia.sumaqagro.platform.farming.domain.repositories.FieldPlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Reads the plot origin from field management.
 * Certification does not use the field plot aggregate outside this adapter.
 *
 * @since 1.0
 */
@Service
@Transactional(readOnly = true)
public class ExternalFieldManagementServiceImpl implements ExternalFieldManagementService {

    private final FieldPlotRepository fieldPlotRepository;

    public ExternalFieldManagementServiceImpl(FieldPlotRepository fieldPlotRepository) {
        this.fieldPlotRepository = fieldPlotRepository;
    }

    @Override
    public Optional<FieldOrigin> findOriginByPlotId(Long plotId) {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot id must be greater than zero");
        }
        return fieldPlotRepository.findById(plotId).map(this::toOrigin);
    }

    private FieldOrigin toOrigin(FieldPlot plot) {
        return new FieldOrigin(plot.getName(), plot.getRegion(), null);
    }
}
