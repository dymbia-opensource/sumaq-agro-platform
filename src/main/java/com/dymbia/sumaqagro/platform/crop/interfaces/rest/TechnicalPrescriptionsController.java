package com.dymbia.sumaqagro.platform.crop.interfaces.rest;

import com.dymbia.sumaqagro.platform.crop.application.commandservices.TechnicalPrescriptionCommandService;
import com.dymbia.sumaqagro.platform.crop.application.queryservices.TechnicalPrescriptionQueryService;
import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.TechnicalPrescription;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetTechnicalPrescriptionByIdQuery;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetTechnicalPrescriptionsByPlotIdQuery;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.IssueTechnicalPrescriptionResource;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.RegisterTechnicalPrescriptionApplicationResource;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.TechnicalPrescriptionResource;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform.IssueTechnicalPrescriptionCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform.RegisterTechnicalPrescriptionApplicationCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform.TechnicalPrescriptionResourceFromEntityAssembler;
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
 * REST controller for issuing, retrieving and applying technical prescriptions.
 */
@RestController
@RequestMapping(value = "/api/v1/technical-prescriptions", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Technical Prescriptions", description = "Technical prescription endpoints")
public class TechnicalPrescriptionsController {

    private final TechnicalPrescriptionCommandService commandService;
    private final TechnicalPrescriptionQueryService queryService;

    public TechnicalPrescriptionsController(
            TechnicalPrescriptionCommandService commandService,
            TechnicalPrescriptionQueryService queryService
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    /**
     * Issues a new technical prescription for a given pest report.
     *
     * @param resource request body containing the pest report ID and agronomist ID
     * @return response entity with the created technical prescription resource
     */
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Issue a technical prescription")
    @ApiResponse(responseCode = "201", description = "Prescription issued",
            content = @Content(schema = @Schema(implementation = TechnicalPrescriptionResource.class)))
    public ResponseEntity<?> issueTechnicalPrescription(@RequestBody IssueTechnicalPrescriptionResource resource) {
        var command = IssueTechnicalPrescriptionCommandFromResourceAssembler
                .toCommandFromResource(resource);
        return toPrescriptionResponse(commandService.handle(command), HttpStatus.CREATED);
    }

    /**
     * Gets a technical prescription by its ID.
     *
     * @param technicalPrescriptionId the ID of the technical prescription to retrieve
     * @return response entity with the requested technical prescription resource
     */
    @GetMapping("/{technicalPrescriptionId}")
    @Operation(summary = "Get a technical prescription by ID")
    public ResponseEntity<TechnicalPrescriptionResource> getTechnicalPrescriptionById(@PathVariable("technicalPrescriptionId") Long technicalPrescriptionId) {
        var query = new GetTechnicalPrescriptionByIdQuery(technicalPrescriptionId);
        var prescription = queryService.handle(query);

        if (prescription.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var resource = TechnicalPrescriptionResourceFromEntityAssembler
                .toResourceFromEntity(prescription.get());
        return ResponseEntity.ok(resource);
    }

    /**
     * Gets all technical prescriptions for a given plot ID.
     *
     * @param plotId the ID of the plot to retrieve technical prescriptions for
     * @return response entity with a list of technical prescription resources
     */
    @GetMapping
    @Operation(summary = "Get technical prescriptions by plot ID")
    public ResponseEntity<List<TechnicalPrescriptionResource>> getTechnicalPrescriptionsByPlotId(@RequestParam("plotId") Long plotId) {
        var query = new GetTechnicalPrescriptionsByPlotIdQuery(plotId);
        var resources = queryService.handle(query)
                .stream()
                .map(TechnicalPrescriptionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }

    /**
     * Registers the application of a technical prescription.
     *
     * @param technicalPrescriptionId the ID of the technical prescription to register application for
     * @return response entity with the updated technical prescription resource
     */
    @PatchMapping("/{technicalPrescriptionId}/application")
    @Operation(summary = "Register the application of a technical prescription")
    @ApiResponse(responseCode = "200", description = "Prescription application registered",
            content = @Content(schema = @Schema(implementation = TechnicalPrescriptionResource.class)))
    public ResponseEntity<?> registerTechnicalPrescriptionApplication(@PathVariable("technicalPrescriptionId") Long technicalPrescriptionId) {
        var resource = new RegisterTechnicalPrescriptionApplicationResource(technicalPrescriptionId);
        var command = RegisterTechnicalPrescriptionApplicationCommandFromResourceAssembler
                .toCommandFromResource(resource);
        return toPrescriptionResponse(commandService.handle(command), HttpStatus.OK);
    }

    private ResponseEntity<?> toPrescriptionResponse(Result<Long, ApplicationError> commandResult, HttpStatus successStatus) {
        var result = commandResult.flatMap(prescriptionId -> queryService
                .handle(new GetTechnicalPrescriptionByIdQuery(prescriptionId))
                .<Result<TechnicalPrescription, ApplicationError>>map(Result::success)
                .orElseGet(() -> Result.failure(
                        ApplicationError.notFound("TechnicalPrescription", prescriptionId.toString())
                )));

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                TechnicalPrescriptionResourceFromEntityAssembler::toResourceFromEntity,
                successStatus
        );
    }
}
