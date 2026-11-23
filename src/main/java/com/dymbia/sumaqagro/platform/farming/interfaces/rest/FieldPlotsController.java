package com.dymbia.sumaqagro.platform.farming.interfaces.rest;
import com.dymbia.sumaqagro.platform.farming.application.commandservices.FieldPlotCommandService;
import com.dymbia.sumaqagro.platform.farming.application.queryservices.FieldPlotQueryService;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetAllFieldPlotsByOwnerUserIdQuery;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetFieldPlotByIdQuery;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.DelineatePlotBoundaryResource;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.FieldPlotResource;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.RegisterFieldPlotResource;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.SoilBaselineResource;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform.RecordSoilBaselineCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform.DelineatePlotBoundaryCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform.FieldPlotResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform.RegisterFieldPlotCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.FieldPlot;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.dymbia.sumaqagro.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller for field plots.
 * It only translates HTTP: the rules live in the services and the errors are built by the shared assemblers.
 */
@RestController
@RequestMapping(value = "/api/v1/field-plots", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Field Plots", description = "Field plot registration and lookup endpoints")
public class FieldPlotsController {
    private final FieldPlotCommandService fieldPlotCommandService;
    private final FieldPlotQueryService fieldPlotQueryService;

    public FieldPlotsController(FieldPlotCommandService fieldPlotCommandService,
                                FieldPlotQueryService fieldPlotQueryService) {
        this.fieldPlotCommandService = fieldPlotCommandService;
        this.fieldPlotQueryService = fieldPlotQueryService;
    }

    /**
     * Registers a field plot.
     *
     * @param resource the plot data sent by the client
     * @return 201 with the new plot, or the problem detail of the error (400, 409 or 422)
     */
    @Operation(summary = "Register a field plot",
            description = "Registers a plot for the authenticated producer, or for a member of the cooperative of the "
                    + "authenticated director. The plot quota of the current subscription is checked.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Field plot registered",
                    content = @Content(schema = @Schema(implementation = FieldPlotResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid plot data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Role or owner does not match"),
            @ApiResponse(responseCode = "404", description = "Cooperative member not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - The producer already has a plot with this name"),
            @ApiResponse(responseCode = "422", description = "No current subscription or plot quota reached")
    })
    @PostMapping
    public ResponseEntity<?> registerFieldPlot(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl principal,
            @RequestBody RegisterFieldPlotResource resource
    ) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String requiredRole = resource.cooperativeId() == null ? "ROLE_FARMER" : "ROLE_COOPERATIVE_DIRECTOR";
        if (principal.getAuthorities().stream()
                .noneMatch(authority -> requiredRole.equals(authority.getAuthority()))
                || !principal.getId().equals(resource.ownerUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        var command = RegisterFieldPlotCommandFromResourceAssembler.toCommandFromResource(resource);
        return toResponseWithFieldPlot(fieldPlotCommandService.handle(command), HttpStatus.CREATED);
    }

    /**
     * Gets a field plot by its id.
     *
     * @param fieldPlotId the plot id
     * @return 200 with the plot, or 404 when it does not exist
     */
    @Operation(summary = "Get a field plot by ID", description = "Retrieves a field plot by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Field plot found",
                    content = @Content(schema = @Schema(implementation = FieldPlotResource.class))),
            @ApiResponse(responseCode = "404", description = "Field plot not found")
    })
    @GetMapping("{fieldPlotId}")
    public ResponseEntity<?> getFieldPlotById(
            @PathVariable
            @Parameter(description = "Unique field plot identifier", example = "1", required = true)
            Long fieldPlotId) {
        return fieldPlotQueryService.handle(new GetFieldPlotByIdQuery(fieldPlotId)).
                <ResponseEntity<?>>map(fieldPlot -> ResponseEntity.ok(FieldPlotResourceFromEntityAssembler.
                toResourceFromEntity(fieldPlot))).orElseGet(() -> ProblemDetailFromApplicationErrorAssembler.
                toResponseFromApplicationError(ApplicationError.notFound("FieldPlot", fieldPlotId.toString())));
    }

    /**
     * Gets the field plots of a producer.
     *
     * @param ownerUserId the producer id
     * @return 200 with the plots, or an empty list when the producer has none
     */
    @Operation(summary = "Get the field plots of a producer", description = "Retrieves every plot owned by a producer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Field plots retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FieldPlotResource.class)))),
            @ApiResponse(responseCode = "400", description = "Missing or invalid owner user ID")
    })
    @GetMapping
    public ResponseEntity<List<FieldPlotResource>> getFieldPlotsByOwnerUserId(
            @RequestParam
            @Parameter(description = "Identifier of the producer that owns the plots", example = "1", required = true)
            Long ownerUserId) {
        var fieldPlots = fieldPlotQueryService.handle(new GetAllFieldPlotsByOwnerUserIdQuery(ownerUserId));
        return ResponseEntity.ok(fieldPlots.stream().map(FieldPlotResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    /**
     * Draws or redraws the polygon of a field plot.
     *
     * @param fieldPlotId the plot id
     * @param resource    the coordinates of the polygon
     * @return 200 with the plot, or the problem detail of the error (400 or 404)
     */
    @Operation(summary = "Delineate the polygon of a field plot",
            description = "Draws or redraws the GPS polygon of the plot, calculates its area in hectares and "
                    + "registers it in AgroMonitoring for satellite monitoring.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Polygon delineated",
                    content = @Content(schema = @Schema(implementation = FieldPlotResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid polygon: fewer than 3 points, crossed edges or area out of range"),
            @ApiResponse(responseCode = "404", description = "Field plot not found")
    })
    @PutMapping("{fieldPlotId}/polygon")
    public ResponseEntity<?> delineateBoundary(
            @PathVariable
            @Parameter(description = "Unique field plot identifier", example = "1", required = true)
            Long fieldPlotId,
            @RequestBody DelineatePlotBoundaryResource resource) {
        var command = DelineatePlotBoundaryCommandFromResourceAssembler.toCommandFromResource(fieldPlotId, resource);
        return toResponseWithFieldPlot(fieldPlotCommandService.handle(command), HttpStatus.OK);
    }

    /**
     * Records the initial soil analysis of a field plot. A new analysis replaces the previous one.
     *
     * @param fieldPlotId the plot id
     * @param resource    the soil analysis
     * @return 200 with the plot, or the problem detail of the error (400 or 404)
     */
    @Operation(summary = "Record the soil baseline of a field plot",
            description = "Records the initial soil analysis of the plot. A new analysis replaces the previous one.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Soil baseline recorded",
                    content = @Content(schema = @Schema(implementation = FieldPlotResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid soil analysis"),
            @ApiResponse(responseCode = "404", description = "Field plot not found")
    })
    @PutMapping("{fieldPlotId}/soil-baseline")
    public ResponseEntity<?> recordSoilBaseline(
            @PathVariable
            @Parameter(description = "Unique field plot identifier", example = "1", required = true)
            Long fieldPlotId,
            @RequestBody SoilBaselineResource resource) {
        var command = RecordSoilBaselineCommandFromResourceAssembler.toCommandFromResource(fieldPlotId, resource);
        return toResponseWithFieldPlot(fieldPlotCommandService.handle(command), HttpStatus.OK);
    }

    /**
     * Reads the field plot back through the query service, because the commands only return its id (CQRS).
     *
     * @param commandResult the result of the command, with the plot id
     * @param successStatus the HTTP status of a successful response
     * @return the plot response, or the problem detail of the error
     */
    private ResponseEntity<?> toResponseWithFieldPlot(Result<Long, ApplicationError> commandResult,
                                                      HttpStatusCode successStatus) {
        Result<FieldPlot, ApplicationError> result =
                commandResult.flatMap(fieldPlotId -> fieldPlotQueryService.handle(new GetFieldPlotByIdQuery(fieldPlotId)).<Result<FieldPlot,
                        ApplicationError>>map(Result::success).orElseGet(() -> Result.failure(ApplicationError.notFound(
                                "FieldPlot", fieldPlotId.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(result,
                FieldPlotResourceFromEntityAssembler::toResourceFromEntity, successStatus);
    }
}
