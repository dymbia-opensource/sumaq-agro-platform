package com.dymbia.sumaqagro.platform.crop.interfaces.rest;

import com.dymbia.sumaqagro.platform.crop.application.commandservices.SatelliteObservationCommandService;
import com.dymbia.sumaqagro.platform.crop.application.queryservices.SatelliteObservationQueryService;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetSatelliteObservationsByPlotIdQuery;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.ImportSatelliteObservationResource;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.SatelliteObservationResource;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform.ImportSatelliteObservationCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform.SatelliteObservationResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller for importing and querying satellite observations.
 */
@RestController
@RequestMapping(
        value = "/api/v1/satellite-observations",
        produces = APPLICATION_JSON_VALUE
)
public class SatelliteObservationsController {

    private final SatelliteObservationQueryService queryService;
    private final SatelliteObservationCommandService commandService;

    public SatelliteObservationsController(
            SatelliteObservationQueryService queryService,
            SatelliteObservationCommandService commandService
    ) {
        this.queryService = queryService;
        this.commandService = commandService;
    }

    /**
     * Imports satellite observations for a given plot and time range.
     *
     * @param resource the request body containing the plot ID and time range
     * @param authenticatedUserId the ID of the authenticated user
     * @return a response entity containing the imported satellite observation resource
     */
    @PostMapping(value = "/import", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> importSatelliteObservation(
            @Valid @RequestBody ImportSatelliteObservationResource resource,
            @AuthenticationPrincipal(expression =
                    "#this instanceof T(org.springframework.security.core.userdetails.UserDetails) ? id : null")
            Long authenticatedUserId
    ) {
        if (authenticatedUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var command = ImportSatelliteObservationCommandFromResourceAssembler.toCommandFromResource(resource, authenticatedUserId);
        var result = commandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                SatelliteObservationResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }

    /**
     * Retrieves satellite observations for a given plot ID.
     *
     * @param plotId the ID of the plot
     * @return a response entity containing a list of satellite observation resources
     */
    @GetMapping
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