package com.dymbia.sumaqagro.platform.crop.interfaces.rest;

import com.dymbia.sumaqagro.platform.crop.application.commandservices.FieldInspectionCommandService;
import com.dymbia.sumaqagro.platform.crop.application.queryservices.FieldInspectionQueryService;
import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.FieldInspection;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetAllFieldInspectionsByPlotIdQuery;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetFieldInspectionByIdQuery;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.CompleteFieldInspectionResource;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.FieldInspectionResource;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.ScheduleFieldInspectionResource;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform.CompleteFieldInspectionCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform.FieldInspectionResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform.ScheduleFieldInspectionCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller for scheduling, retrieving and completing field inspections.
 */
@RestController
@RequestMapping(value = "/api/v1/field-inspections", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Field Inspections", description = "Field inspection management endpoints")
public class FieldInspectionsController {

    private final FieldInspectionCommandService commandService;
    private final FieldInspectionQueryService queryService;

    public FieldInspectionsController(
            FieldInspectionCommandService commandService,
            FieldInspectionQueryService queryService
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    /**
     * Schedules an inspection and retrieves its information for the response.
     */
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Schedule a field inspection")
    @ApiResponse(responseCode = "201", description = "Inspection scheduled",
            content = @Content(schema = @Schema(implementation = FieldInspectionResource.class)))
    public ResponseEntity<?> scheduleFieldInspection(
            @RequestBody ScheduleFieldInspectionResource resource
    ) {
        var command = ScheduleFieldInspectionCommandFromResourceAssembler
                .toCommandFromResource(resource);

        var result = commandService.handle(command)
                .flatMap(fieldInspectionId -> queryService
                        .handle(new GetFieldInspectionByIdQuery(fieldInspectionId))
                        .<Result<FieldInspection, ApplicationError>>map(Result::success)
                        .orElseGet(() -> Result.failure(
                                ApplicationError.notFound(
                                        "FieldInspection",
                                        fieldInspectionId.toString()
                                )
                        )));

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                FieldInspectionResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    /**
     * Retrieves an inspection by its identifier.
     */
    @GetMapping("/{fieldInspectionId}")
    @Operation(summary = "Get a field inspection by ID")
    public ResponseEntity<FieldInspectionResource> getFieldInspectionById(
            @PathVariable("fieldInspectionId") Long fieldInspectionId
    ) {
        var query = new GetFieldInspectionByIdQuery(fieldInspectionId);
        var result = queryService.handle(query);

        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var resource = FieldInspectionResourceFromEntityAssembler
                .toResourceFromEntity(result.get());

        return ResponseEntity.ok(resource);
    }

    /**
     * Retrieves inspections associated with a plot.
     */
    @GetMapping
    @Operation(summary = "Get all field inspections by plot ID")
    public ResponseEntity<List<FieldInspectionResource>> getAllFieldInspectionsByPlotId(@RequestParam("plotId") Long plotId) {
        var query = new GetAllFieldInspectionsByPlotIdQuery(plotId);
        var inspections = queryService.handle(query);

        var resources = inspections.stream()
                .map(FieldInspectionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }

    /**
     * Completes an inspection and retrieves its updated information.
     */
    @PatchMapping(value = "/{fieldInspectionId}/completion", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Complete a field inspection")
    @ApiResponse(responseCode = "200", description = "Inspection completed",
            content = @Content(schema = @Schema(implementation = FieldInspectionResource.class)))
    public ResponseEntity<?> completeFieldInspection(
            @PathVariable("fieldInspectionId") Long fieldInspectionId,
            @RequestBody CompleteFieldInspectionResource resource
    ) {
        var command = CompleteFieldInspectionCommandFromResourceAssembler
                .toCommandFromResource(fieldInspectionId, resource);

        var result = commandService.handle(command)
                .flatMap(inspectionId -> queryService
                        .handle(new GetFieldInspectionByIdQuery(inspectionId))
                        .<Result<FieldInspection, ApplicationError>>map(Result::success)
                        .orElseGet(() -> Result.failure(
                                ApplicationError.notFound(
                                        "FieldInspection",
                                        inspectionId.toString()
                                )
                        )));

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                FieldInspectionResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }
}
