package com.dymbia.sumaqagro.platform.profiles.interfaces.rest;

import com.dymbia.sumaqagro.platform.profiles.application.commandservices.AgronomistAssignmentCommandService;
import com.dymbia.sumaqagro.platform.profiles.application.queryservices.ProfilesQueryService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetAgronomistAssignedToPlotQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetAgronomistAssignmentsByCooperativeQuery;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.AgronomistAssignmentResource;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.AssignAgronomistToPlotResource;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform.AgronomistAssignmentResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform.AssignAgronomistToPlotCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Agronomist Assignment operations.
 *
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/api/v1/agronomist-assignments", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Agronomist Assignments", description = "Assign agronomists to plots")
public class AgronomistAssignmentsController {

    private final AgronomistAssignmentCommandService agronomistAssignmentCommandService;
    private final ProfilesQueryService profilesQueryService;

    public AgronomistAssignmentsController(AgronomistAssignmentCommandService agronomistAssignmentCommandService,
                                           ProfilesQueryService profilesQueryService) {
        this.agronomistAssignmentCommandService = agronomistAssignmentCommandService;
        this.profilesQueryService = profilesQueryService;
    }

    @Operation(summary = "Assign an agronomist to a plot")
    @PostMapping
    public ResponseEntity<?> assignAgronomistToPlot(@RequestBody AssignAgronomistToPlotResource resource) {
        var command = AssignAgronomistToPlotCommandFromResourceAssembler.toCommandFromResource(resource);
        Result<AgronomistAssignment, ApplicationError> result = agronomistAssignmentCommandService.handle(command);

        if (result.isSuccess()) {
            AgronomistAssignment assignment = ((Result.Success<AgronomistAssignment, ApplicationError>) result).value();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(AgronomistAssignmentResourceFromEntityAssembler.toResourceFromEntity(assignment));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Operation(summary = "Get the agronomist assigned to a plot")
    @GetMapping("/plot/{plotId}")
    public ResponseEntity<AgronomistAssignmentResource> getByPlotId(@PathVariable Long plotId) {
        return profilesQueryService.handle(new GetAgronomistAssignedToPlotQuery(plotId))
                .map(assignment -> ResponseEntity.ok(
                        AgronomistAssignmentResourceFromEntityAssembler.toResourceFromEntity(assignment)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all agronomist assignments of a cooperative")
    @GetMapping
    public ResponseEntity<List<AgronomistAssignmentResource>> getByCooperativeId(
            @RequestParam Long cooperativeId) {

        List<AgronomistAssignment> assignments = profilesQueryService.handle(
                new GetAgronomistAssignmentsByCooperativeQuery(cooperativeId));

        var resources = assignments.stream()
                .map(AgronomistAssignmentResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }
}