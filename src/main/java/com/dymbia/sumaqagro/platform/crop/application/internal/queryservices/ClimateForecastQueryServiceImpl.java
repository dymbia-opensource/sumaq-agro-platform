package com.dymbia.sumaqagro.platform.crop.application.internal.queryservices;

import com.dymbia.sumaqagro.platform.crop.application.queryservices.ClimateForecastQueryService;
import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.ClimateForecast;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetClimateForecastsByRegionQuery;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.ClimateForecastRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClimateForecastQueryServiceImpl implements ClimateForecastQueryService {

    private final ClimateForecastRepository forecastRepository;

    public ClimateForecastQueryServiceImpl(ClimateForecastRepository forecastRepository) {
        this.forecastRepository = forecastRepository;
    }

    @Override
    public List<ClimateForecast> handle(GetClimateForecastsByRegionQuery query) {
        return forecastRepository.findAllByRegion(query.region());
    }
}