package com.dymbia.sumaqagro.platform.profiles.interfaces.rest;

import com.dymbia.sumaqagro.platform.profiles.application.commandservices.AgronomistAssignmentCommandService;
import com.dymbia.sumaqagro.platform.profiles.application.queryservices.AgronomistAssignmentQueryService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.*;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.AgronomistAssignmentResource;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.AssignAgronomistToPlotResource;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.ReassignAgronomistResource;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform.AgronomistAssignmentResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform.AssignAgronomistToPlotCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform.ReassignAgronomistCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
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
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/agronomist-assignments", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Agronomist Assignments", description = "Assignment of agronomists to the plots of cooperative members")
public class AgronomistAssignmentsController {

    private final AgronomistAssignmentCommandService agronomistAssignmentCommandService;
    private final AgronomistAssignmentQueryService agronomistAssignmentQueryService;

    public AgronomistAssignmentsController(AgronomistAssignmentCommandService agronomistAssignmentCommandService,
                                           AgronomistAssignmentQueryService agronomistAssignmentQueryService) {
        this.agronomistAssignmentCommandService = agronomistAssignmentCommandService;
        this.agronomistAssignmentQueryService = agronomistAssignmentQueryService;
    }

    @PostMapping
    @Operation(summary = "Assign an agronomist to a plot",
            description = "The cooperative director assigns an agronomist to the plot of a member (US-20). "
                    + "The plot owner is read from Field Management and must be an active member of the cooperative.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Agronomist assigned",
                    content = @Content(schema = @Schema(implementation = AgronomistAssignmentResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or user is not an agronomist"),
            @ApiResponse(responseCode = "404", description = "Cooperative or plot not found"),
            @ApiResponse(responseCode = "409", description = "The plot already has an active assignment"),
            @ApiResponse(responseCode = "422", description = "The plot owner is not a member of the cooperative")
    })
    public ResponseEntity<?> assignAgronomistToPlot(@Valid @RequestBody AssignAgronomistToPlotResource resource) {
        var command = AssignAgronomistToPlotCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = agronomistAssignmentCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, AgronomistAssignmentResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @GetMapping("/{assignmentId}")
    @Operation(summary = "Get assignment by ID", description = "Retrieves an agronomist assignment by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignment found",
                    content = @Content(schema = @Schema(implementation = AgronomistAssignmentResource.class))),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    public ResponseEntity<?> getAssignmentById(
            @PathVariable
            @Parameter(description = "Assignment unique identifier", example = "1", required = true)
            Long assignmentId) {
        var assignment = agronomistAssignmentQueryService.handle(new GetAgronomistAssignmentByIdQuery(assignmentId));
        if (assignment.isEmpty()) {
            var error = ApplicationError.notFound("AgronomistAssignment", assignmentId.toString());
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
        }
        return ResponseEntity.ok(AgronomistAssignmentResourceFromEntityAssembler.toResourceFromEntity(assignment.get()));
    }

    @GetMapping
    @Operation(summary = "Find assignments",
            description = "Send at most one filter: cooperativeId, producerUserId, agronomistUserId or plotId. "
                    + "Without filters it returns every assignment. Always returns a list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignments retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AgronomistAssignmentResource.class))))
    })
    public ResponseEntity<List<AgronomistAssignmentResource>> getAssignments(
            @RequestParam(required = false) @Parameter(description = "Cooperative id", example = "1") Long cooperativeId,
            @RequestParam(required = false) @Parameter(description = "Plot owner user id", example = "1") Long producerUserId,
            @RequestParam(required = false) @Parameter(description = "Agronomist user id", example = "4") Long agronomistUserId,
            @RequestParam(required = false) @Parameter(description = "Plot id", example = "1") Long plotId) {
        List<AgronomistAssignment> assignments;
        if (cooperativeId != null) {
            assignments = agronomistAssignmentQueryService.handle(new GetAgronomistAssignmentsByCooperativeQuery(cooperativeId));
        } else if (producerUserId != null) {
            assignments = agronomistAssignmentQueryService.handle(new GetAgronomistAssignmentsByProducerUserIdQuery(producerUserId));
        } else if (agronomistUserId != null) {
            assignments = agronomistAssignmentQueryService.handle(new GetAgronomistAssignmentsByAgronomistUserIdQuery(agronomistUserId));
        } else if (plotId != null) {
            assignments = agronomistAssignmentQueryService.handle(new GetAgronomistAssignmentsByPlotIdQuery(plotId));
        } else {
            assignments = agronomistAssignmentQueryService.handle(new GetAllAgronomistAssignmentsQuery());
        }
        return ResponseEntity.ok(assignments.stream()
                .map(AgronomistAssignmentResourceFromEntityAssembler::toResourceFromEntity)
                .toList());
    }

    @PutMapping("/{assignmentId}")
    @Operation(summary = "Reassign the plot to another agronomist",
            description = "Gives the plot to another agronomist. The previous agronomist is saved in the history.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignment updated",
                    content = @Content(schema = @Schema(implementation = AgronomistAssignmentResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or user is not an agronomist"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    public ResponseEntity<?> reassignAgronomist(
            @PathVariable
            @Parameter(description = "Assignment unique identifier", example = "1", required = true)
            Long assignmentId,
            @Valid @RequestBody ReassignAgronomistResource resource) {
        var command = ReassignAgronomistCommandFromResourceAssembler.toCommandFromResource(assignmentId, resource);
        var result = agronomistAssignmentCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, AgronomistAssignmentResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}
