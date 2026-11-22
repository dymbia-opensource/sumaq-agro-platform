package com.dymbia.sumaqagro.platform.crop.interfaces.rest;

import com.dymbia.sumaqagro.platform.crop.application.commandservices.ClimateForecastCommandService;
import com.dymbia.sumaqagro.platform.crop.application.queryservices.ClimateForecastQueryService;
import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.ClimateForecast;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.ImportClimateForecastCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetClimateForecastsByRegionQuery;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.ClimateForecastResource;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.ImportClimateForecastResource;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform.ClimateForecastResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
 * REST controller for climate forecasts: lookup by region and import from AgroMonitoring.
 */
@RestController
@RequestMapping(value = "/api/v1/climate-forecasts", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Climate Forecasts", description = "Climate forecasts by region")
public class ClimateForecastsController {

    private final ClimateForecastQueryService queryService;
    private final ClimateForecastCommandService commandService;

    public ClimateForecastsController(ClimateForecastQueryService queryService,
                                      ClimateForecastCommandService commandService) {
        this.queryService = queryService;
        this.commandService = commandService;
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

    /**
     * Imports the 5-day weather forecast of one of your plots from AgroMonitoring.
     * It records one forecast per day for the plot region and issues frost or water stress alerts.
     *
     * @param resource            the plot
     * @param authenticatedUserId the producer that asks for it
     * @return 200 with the daily forecasts, or the problem detail of the error (401, 404, 409 or 500)
     */
    @PostMapping(value = "/imports", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Import the weather forecast of a plot from AgroMonitoring",
            description = "Reads the 5-day forecast of the plot polygon, records one climate forecast per day "
                    + "(average temperature and share of 3-hour intervals with rain) and issues frost or water "
                    + "stress alerts for the plot region. Read them with GET /api/v1/agroclimatic-alerts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Forecast imported",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ClimateForecastResource.class)))),
            @ApiResponse(responseCode = "404", description = "Plot not found, not yours, or no forecast available"),
            @ApiResponse(responseCode = "409", description = "The plot has no monitoring polygon")
    })
    public ResponseEntity<?> importClimateForecast(
            @Valid @RequestBody ImportClimateForecastResource resource,
            @AuthenticationPrincipal(expression =
                    "#this instanceof T(org.springframework.security.core.userdetails.UserDetails) ? id : null")
            Long authenticatedUserId) {
        if (authenticatedUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var command = new ImportClimateForecastCommand(resource.plotId(), authenticatedUserId);
        return ResponseEntityAssembler.toResponseEntityFromResult(commandService.handle(command),
                (List<ClimateForecast> forecasts) -> forecasts.stream()
                        .map(ClimateForecastResourceFromEntityAssembler::toResourceFromEntity).toList(),
                HttpStatus.OK);
    }
}
