package com.dymbia.sumaqagro.platform.crop.interfaces.rest;

import com.dymbia.sumaqagro.platform.crop.application.queryservices.SatelliteObservationQueryService;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetSatelliteObservationsByPlotIdQuery;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.SatelliteObservationResource;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform.SatelliteObservationResourceFromEntityAssembler;
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
 * REST controller for querying satellite observations.
 */
@RestController
@RequestMapping(
        value = "/api/v1/satellite-observations",
        produces = APPLICATION_JSON_VALUE
)
@Tag(name = "Satellite Observations", description = "Satellite observations by plot")
public class SatelliteObservationsController {

    private final SatelliteObservationQueryService queryService;

    public SatelliteObservationsController(SatelliteObservationQueryService queryService) {
        this.queryService = queryService;
    }

    /**
     * Retrieves satellite observations for a specific plot.
     *
     * @param plotId the ID of the plot
     * @return a list of satellite observation resources
     */
    @GetMapping
    @Operation(summary = "Get satellite observations by plot ID")
    public ResponseEntity<List<SatelliteObservationResource>> getSatelliteObservationsByPlotId(
            @RequestParam("plotId") Long plotId
    ) {
        var query = new GetSatelliteObservationsByPlotIdQuery(plotId);

        var resources = queryService.handle(query)
                .stream()
                .map(SatelliteObservationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }
}