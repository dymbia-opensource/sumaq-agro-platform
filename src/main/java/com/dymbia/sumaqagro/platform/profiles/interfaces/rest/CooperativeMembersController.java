package com.dymbia.sumaqagro.platform.profiles.interfaces.rest;

import com.dymbia.sumaqagro.platform.profiles.application.queryservices.CooperativeQueryService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.CooperativeMember;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetCooperativeMembersByCooperativeIdQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetCooperativeMembersByUserIdQuery;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.CooperativeMemberResource;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform.CooperativeMemberResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Read-only member lookups used by the frontend (member list of a cooperative and memberships of a user).
 * Members are created through {@code POST /api/v1/cooperatives/{cooperativeId}/members}.
 *
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/api/v1/cooperative-members", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Cooperatives", description = "Cooperative registration, institutional data, members and agronomist invitations")
public class CooperativeMembersController {

    private final CooperativeQueryService cooperativeQueryService;

    public CooperativeMembersController(CooperativeQueryService cooperativeQueryService) {
        this.cooperativeQueryService = cooperativeQueryService;
    }

    @GetMapping
    @Operation(summary = "Find cooperative members",
            description = "Send 'cooperativeId' for the member list of a cooperative, or 'userId' for the memberships of a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Members retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CooperativeMemberResource.class)))),
            @ApiResponse(responseCode = "400", description = "Neither cooperativeId nor userId was sent")
    })
    public ResponseEntity<?> getMembers(
            @RequestParam(required = false)
            @Parameter(description = "Cooperative id", example = "1")
            Long cooperativeId,
            @RequestParam(required = false)
            @Parameter(description = "IAM user id of the member", example = "1")
            Long userId) {
        List<CooperativeMember> members;
        if (cooperativeId != null) {
            members = cooperativeQueryService.handle(new GetCooperativeMembersByCooperativeIdQuery(cooperativeId));
        } else if (userId != null) {
            members = cooperativeQueryService.handle(new GetCooperativeMembersByUserIdQuery(userId));
        } else {
            var error = ApplicationError.validationError("request-parameters", "Send cooperativeId or userId");
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
        }
        return ResponseEntity.ok(members.stream()
                .map(CooperativeMemberResourceFromEntityAssembler::toResourceFromEntity)
                .toList());
    }
}
