package com.dymbia.sumaqagro.platform.profiles.interfaces.rest;

import com.dymbia.sumaqagro.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;

import com.dymbia.sumaqagro.platform.profiles.application.commandservices.CooperativeCommandService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.AgronomistInvitation;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.*;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform.*;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Cooperative operations.
 *
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/api/v1/cooperatives", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Cooperatives", description = "Cooperative registration, members and agronomist invitations")
public class CooperativesController {

    private final CooperativeCommandService cooperativeCommandService;

    public CooperativesController(CooperativeCommandService cooperativeCommandService) {
        this.cooperativeCommandService = cooperativeCommandService;
    }

    @Operation(summary = "Register a new cooperative")
    @PostMapping
    public ResponseEntity<?> registerCooperative(@RequestBody RegisterCooperativeResource resource) {
        var command = RegisterCooperativeCommandFromResourceAssembler.toCommandFromResource(resource);
        Result<Cooperative, ApplicationError> result = cooperativeCommandService.handle(command);

        if (result.isSuccess()) {
            Cooperative cooperative = ((Result.Success<Cooperative, ApplicationError>) result).value();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CooperativeResourceFromEntityAssembler.toResourceFromEntity(cooperative));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Operation(summary = "Add a member to a cooperative")
    @PostMapping("/{cooperativeId}/members")
    public ResponseEntity<?> addMember(@PathVariable Long cooperativeId,
                                       @AuthenticationPrincipal UserDetailsImpl principal,
                                       @RequestBody AddCooperativeMemberResource resource) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (principal.getAuthorities().stream()
                .noneMatch(authority -> "ROLE_COOPERATIVE_DIRECTOR".equals(authority.getAuthority()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var command = AddCooperativeMemberCommandFromResourceAssembler.toCommandFromResource(
                cooperativeId, principal.getId(), resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                cooperativeCommandService.handle(command),
                CooperativeMemberResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    @Operation(summary = "Invite an agronomist to a cooperative")
    @PostMapping("/{cooperativeId}/invitations")
    public ResponseEntity<?> inviteAgronomist(@PathVariable Long cooperativeId,
                                              @RequestBody InviteAgronomistResource resource) {
        var command = InviteAgronomistCommandFromResourceAssembler.toCommandFromResource(cooperativeId, resource);
        Result<AgronomistInvitation, ApplicationError> result = cooperativeCommandService.handle(command);

        if (result.isSuccess()) {
            AgronomistInvitation invitation = ((Result.Success<AgronomistInvitation, ApplicationError>) result).value();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(AgronomistInvitationResourceFromEntityAssembler.toResourceFromEntity(invitation));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
