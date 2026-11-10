package com.dymbia.sumaqagro.platform.crop.application.internal.queryservices;

import com.dymbia.sumaqagro.platform.crop.application.queryservices.PestReportQueryService;
import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.PestReport;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetAllPestReportsByPlotIdQuery;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetPestReportByIdQuery;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.PestReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Application service that resolves pest report read queries.
 */
@Service
public class PestReportQueryServiceImpl implements PestReportQueryService {

    private final PestReportRepository pestReportRepository;

    public PestReportQueryServiceImpl(PestReportRepository pestReportRepository) {
        this.pestReportRepository = pestReportRepository;
    }

    @Override
    public Optional<PestReport> handle(GetPestReportByIdQuery query) {
        return pestReportRepository.findById(query.pestReportId());
    }

    @Override
    public List<PestReport> handle(GetAllPestReportsByPlotIdQuery query) {
        return pestReportRepository.findAllByPlotId(query.plotId());
    }
}