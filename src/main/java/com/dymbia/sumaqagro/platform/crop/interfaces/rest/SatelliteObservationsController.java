package com.dymbia.sumaqagro.platform.crop.interfaces.rest;

import com.dymbia.sumaqagro.platform.crop.application.commandservices.SatelliteObservationCommandService;
import com.dymbia.sumaqagro.platform.crop.application.queryservices.SatelliteObservationQueryService;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetSatelliteObservationsByPlotIdQuery;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.ImportSatelliteObservationResource;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.RequestSatelliteObservationImportResource;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.SatelliteObservationResource;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform.ImportSatelliteObservationCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform.RequestSatelliteObservationImportCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform.SatelliteObservationResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Satellite Observations", description = "Satellite observations by plot")
@SecurityScheme(name = "sumaqAgroBearer", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
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
    @PostMapping(value = "/imports", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Import the latest satellite observation in a date range",
            security = @SecurityRequirement(name = "sumaqAgroBearer"))
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
     * Searches the configured archive and returns the saved observation when the search completes.
     *
     * @param resource the plot to import
     * @param authenticatedUserId the authenticated owner of the plot
     * @return the persisted observation or an application error
     */
    @PostMapping(value = "/imports/latest", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Import the latest available satellite observation",
            description = "Searches backwards to the configured archive start and waits for completion. "
                    + "Prefers a capture containing both NDVI and NDWI, otherwise returns the latest partial capture. "
                    + "capturedAt is the original satellite capture time, including historical years.",
            security = @SecurityRequirement(name = "sumaqAgroBearer"))
    @ApiResponse(responseCode = "200", description = "Search completed and observation persisted")
    @ApiResponse(responseCode = "400", description = "Invalid request body or plot ID")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Plot unavailable to the requester, or no usable captures found")
    @ApiResponse(responseCode = "409", description = "Monitoring polygon missing or changed during import")
    @ApiResponse(responseCode = "500", description = "Satellite provider or persistence failed")
    public ResponseEntity<?> importLatestSatelliteObservation(
            @Valid @RequestBody RequestSatelliteObservationImportResource resource,
            @AuthenticationPrincipal(expression =
                    "#this instanceof T(org.springframework.security.core.userdetails.UserDetails) ? id : null")
            Long authenticatedUserId
    ) {
        if (authenticatedUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var command = RequestSatelliteObservationImportCommandFromResourceAssembler
                .toCommandFromResource(resource, authenticatedUserId);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                commandService.handle(command),
                SatelliteObservationResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }

    /**
     * Retrieves satellite observations for a given plot ID.
     *
     * @param plotId the ID of the plot
     * @return a response entity containing a list of satellite observation resources
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
