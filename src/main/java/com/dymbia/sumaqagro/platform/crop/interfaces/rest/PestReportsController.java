package com.dymbia.sumaqagro.platform.crop.interfaces.rest;

import com.dymbia.sumaqagro.platform.crop.application.commandservices.PestReportCommandService;
import com.dymbia.sumaqagro.platform.crop.application.queryservices.PestReportQueryService;
import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.PestReport;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.DeletePestReportCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetAllPestReportsByPlotIdQuery;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetPestReportByIdQuery;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.PestReportResource;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.RecordPestReportResource;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform.PestReportResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform.RecordPestReportCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.resources.MessageResource;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller for pest report operations.
 */
@RestController
@RequestMapping(value = "/api/v1/pest-reports", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Pest Reports", description = "Pest report management endpoints")
public class PestReportsController {

    private final PestReportCommandService commandService;
    private final PestReportQueryService queryService;

    public PestReportsController(PestReportCommandService commandService, PestReportQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    /**
     * Records a report and retrieves its information for the response.
     */
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Record a pest report")
    public ResponseEntity<?> recordPestReport(
            @RequestBody RecordPestReportResource resource
    ) {
        var command = RecordPestReportCommandFromResourceAssembler
                .toCommandFromResource(resource);

        var result = commandService.handle(command)
                .flatMap(pestReportId -> queryService
                        .handle(new GetPestReportByIdQuery(pestReportId))
                        .<Result<PestReport, ApplicationError>>map(Result::success)
                        .orElseGet(() -> Result.failure(
                                ApplicationError.notFound(
                                        "PestReport",
                                        pestReportId.toString()
                                )
                        )));

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                PestReportResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    /**
     * Retrieves a report by its identifier.
     */
    @GetMapping("/{pestReportId}")
    @Operation(summary = "Get a pest report by ID")
    public ResponseEntity<PestReportResource> getPestReportById(
            @PathVariable("pestReportId") Long pestReportId
    ) {
        var query = new GetPestReportByIdQuery(pestReportId);
        var result = queryService.handle(query);

        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var resource = PestReportResourceFromEntityAssembler
                .toResourceFromEntity(result.get());

        return ResponseEntity.ok(resource);
    }

    /**
     * Retrieves reports associated with a plot.
     */
    @GetMapping
    @Operation(summary = "Get all pest reports by plot ID")
    public ResponseEntity<List<PestReportResource>> getAllPestReportsByPlotId(
            @RequestParam("plotId") Long plotId
    ) {
        var query = new GetAllPestReportsByPlotIdQuery(plotId);
        var reports = queryService.handle(query);

        var resources = reports.stream()
                .map(PestReportResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }

    /**
     * Deletes a report by its identifier.
     */
    @DeleteMapping("/{pestReportId}")
    @Operation(summary = "Delete a pest report")
    public ResponseEntity<?> deletePestReport(
            @PathVariable("pestReportId") Long pestReportId
    ) {
        var command = new DeletePestReportCommand(pestReportId);

        var result = commandService.handle(command)
                .map(id -> new MessageResource(
                        "Pest report with ID %s successfully deleted."
                                .formatted(id)
                ));

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                message -> message,
                HttpStatus.OK
        );
    }
}