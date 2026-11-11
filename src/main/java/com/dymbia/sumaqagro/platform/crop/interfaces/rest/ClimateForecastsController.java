package com.dymbia.sumaqagro.platform.crop.interfaces.rest;

import com.dymbia.sumaqagro.platform.crop.application.queryservices.ClimateForecastQueryService;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetClimateForecastsByRegionQuery;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.ClimateForecastResource;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform.ClimateForecastResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller for retrieving climate forecasts by region.
 */
@RestController
@RequestMapping(value = "/api/v1/climate-forecasts", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Climate Forecasts", description = "Climate forecasts by region")
public class ClimateForecastsController {

    private final ClimateForecastQueryService queryService;

    public ClimateForecastsController(ClimateForecastQueryService queryService) {
        this.queryService = queryService;
    }

    /**
     * Retrieves climate forecasts for a specific region.
     *
     * @param region the region identifier
     * @return list of climate forecasts for the region, or an empty list
     */
    @GetMapping
    @Operation(summary = "Get climate forecasts by region")
    public ResponseEntity<List<ClimateForecastResource>> getClimateForecastsByRegion(@RequestParam("region") String region) {
        var query = new GetClimateForecastsByRegionQuery(region);

        var resources = queryService.handle(query)
                .stream()
                .map(ClimateForecastResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }
}