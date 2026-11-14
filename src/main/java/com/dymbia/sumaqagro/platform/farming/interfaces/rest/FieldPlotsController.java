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
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
    @Operation(summary = "Register a field plot")
    @PostMapping
    public ResponseEntity<?> registerFieldPlot(@RequestBody RegisterFieldPlotResource resource) {
        var command = RegisterFieldPlotCommandFromResourceAssembler.toCommandFromResource(resource);
        return toResponseWithFieldPlot(fieldPlotCommandService.handle(command), HttpStatus.CREATED);
    }

    /**
     * Gets a field plot by its id.
     *
     * @param fieldPlotId the plot id
     * @return 200 with the plot, or 404 when it does not exist
     */
    @Operation(summary = "Get a field plot by ID")
    @GetMapping("{fieldPlotId}")
    public ResponseEntity<?> getFieldPlotById(@PathVariable Long fieldPlotId) {
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
    @Operation(summary = "Get the field plots of a producer")
    @GetMapping
    public ResponseEntity<List<FieldPlotResource>> getFieldPlotsByOwnerUserId(@RequestParam Long ownerUserId) {
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
    @Operation(summary = "Delineate the polygon of a field plot")
    @PutMapping("{fieldPlotId}/polygon")
    public ResponseEntity<?> delineateBoundary(@PathVariable Long fieldPlotId,
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
    @Operation(summary = "Record the soil baseline of a field plot")
    @PutMapping("{fieldPlotId}/soil-baseline")
    public ResponseEntity<?> recordSoilBaseline(@PathVariable Long fieldPlotId,
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
