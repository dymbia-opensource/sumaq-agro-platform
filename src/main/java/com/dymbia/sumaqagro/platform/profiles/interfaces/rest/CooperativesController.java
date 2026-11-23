package com.dymbia.sumaqagro.platform.profiles.interfaces.rest;

import com.dymbia.sumaqagro.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;

import com.dymbia.sumaqagro.platform.profiles.application.commandservices.CooperativeCommandService;
import com.dymbia.sumaqagro.platform.profiles.application.internal.outboundservices.acl.ExternalIamService;
import com.dymbia.sumaqagro.platform.profiles.application.queryservices.CooperativeQueryService;
import com.dymbia.sumaqagro.platform.profiles.application.queryservices.ProfileQueryService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetAllCooperativesQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetCooperativeByIdQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetCooperativeMembersByCooperativeIdQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetCooperativesByManagerUserIdQuery;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.*;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform.*;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/cooperatives", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Cooperatives", description = "Cooperative registration, institutional data, members and agronomist invitations")
public class CooperativesController {

    private final CooperativeCommandService cooperativeCommandService;
    private final CooperativeQueryService cooperativeQueryService;
    private final ProfileQueryService profileQueryService;
    private final ExternalIamService externalIamService;

    public CooperativesController(CooperativeCommandService cooperativeCommandService,
                                  CooperativeQueryService cooperativeQueryService,
                                  ProfileQueryService profileQueryService,
                                  ExternalIamService externalIamService) {
        this.cooperativeCommandService = cooperativeCommandService;
        this.cooperativeQueryService = cooperativeQueryService;
        this.profileQueryService = profileQueryService;
        this.externalIamService = externalIamService;
    }

    @PostMapping
    @Operation(summary = "Register a cooperative",
            description = "Registers a cooperative with its RUC, business name and region (US-17).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cooperative registered",
                    content = @Content(schema = @Schema(implementation = CooperativeResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "RUC already registered")
    })
    public ResponseEntity<?> registerCooperative(@Valid @RequestBody RegisterCooperativeResource resource) {
        var command = RegisterCooperativeCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = cooperativeCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, CooperativeResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @GetMapping("/{cooperativeId}")
    @Operation(summary = "Get cooperative by ID", description = "Retrieves a cooperative by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cooperative found",
                    content = @Content(schema = @Schema(implementation = CooperativeResource.class))),
            @ApiResponse(responseCode = "404", description = "Cooperative not found")
    })
    public ResponseEntity<?> getCooperativeById(
            @PathVariable
            @Parameter(description = "Cooperative unique identifier", example = "1", required = true)
            Long cooperativeId) {
        var cooperative = cooperativeQueryService.handle(new GetCooperativeByIdQuery(cooperativeId));
        if (cooperative.isEmpty()) {
            return notFound(cooperativeId);
        }
        return ResponseEntity.ok(CooperativeResourceFromEntityAssembler.toResourceFromEntity(cooperative.get()));
    }

    @GetMapping
    @Operation(summary = "Get cooperatives",
            description = "Retrieves all cooperatives, or the ones managed by a director when 'directorUserId' is sent.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cooperatives retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CooperativeResource.class))))
    })
    public ResponseEntity<List<CooperativeResource>> getCooperatives(
            @RequestParam(required = false)
            @Parameter(description = "IAM user id of the director (optional filter)", example = "3")
            Long directorUserId) {
        List<Cooperative> cooperatives = directorUserId != null
                ? cooperativeQueryService.handle(new GetCooperativesByManagerUserIdQuery(directorUserId))
                : cooperativeQueryService.handle(new GetAllCooperativesQuery());
        return ResponseEntity.ok(cooperatives.stream()
                .map(CooperativeResourceFromEntityAssembler::toResourceFromEntity)
                .toList());
    }

    @PutMapping("/{cooperativeId}")
    @Operation(summary = "Update cooperative",
            description = "Updates the institutional data of a cooperative (settings view). All fields are required.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cooperative updated",
                    content = @Content(schema = @Schema(implementation = CooperativeResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Cooperative not found"),
            @ApiResponse(responseCode = "409", description = "RUC belongs to another cooperative")
    })
    public ResponseEntity<?> updateCooperative(
            @PathVariable
            @Parameter(description = "Cooperative unique identifier", example = "1", required = true)
            Long cooperativeId,
            @Valid @RequestBody UpdateCooperativeResource resource) {
        var command = UpdateCooperativeCommandFromResourceAssembler.toCommandFromResource(cooperativeId, resource);
        var result = cooperativeCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, CooperativeResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @PostMapping("/{cooperativeId}/members")
    @Operation(summary = "Add a member", description = "Adds a producer to the member list of the cooperative (US-18).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Member added",
                    content = @Content(schema = @Schema(implementation = CooperativeMemberResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or DNI already in the cooperative"),
            @ApiResponse(responseCode = "404", description = "Cooperative not found")
    })
    public ResponseEntity<?> addMember(
            @PathVariable
            @Parameter(description = "Cooperative unique identifier", example = "1", required = true)
            Long cooperativeId,
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody AddCooperativeMemberResource resource) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (principal.getAuthorities().stream()
                .noneMatch(authority -> "ROLE_COOPERATIVE_DIRECTOR".equals(authority.getAuthority()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        var command = AddCooperativeMemberCommandFromResourceAssembler.toCommandFromResource(cooperativeId, principal.getId(), resource);
        var result = cooperativeCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, CooperativeMemberResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @GetMapping("/{cooperativeId}/members")
    @Operation(summary = "Get members of a cooperative", description = "Retrieves the member list of a cooperative.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Members retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CooperativeMemberResource.class)))),
            @ApiResponse(responseCode = "404", description = "Cooperative not found")
    })
    public ResponseEntity<?> getMembers(
            @PathVariable
            @Parameter(description = "Cooperative unique identifier", example = "1", required = true)
            Long cooperativeId) {
        if (cooperativeQueryService.handle(new GetCooperativeByIdQuery(cooperativeId)).isEmpty()) {
            return notFound(cooperativeId);
        }
        var members = cooperativeQueryService.handle(new GetCooperativeMembersByCooperativeIdQuery(cooperativeId));
        return ResponseEntity.ok(members.stream()
                .map(CooperativeMemberResourceFromEntityAssembler::toResourceFromEntity)
                .toList());
    }

    @GetMapping("/{cooperativeId}/member-candidates")
    @Operation(summary = "Look up a producer to add as a member",
            description = "Finds the producer account that uses an email and returns the data of its profile "
                    + "(names, DNI and phone), so the director does not type them again. Only the director of the cooperative may ask.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producer found",
                    content = @Content(schema = @Schema(implementation = MemberCandidateResource.class))),
            @ApiResponse(responseCode = "404", description = "Cooperative not found or no producer account uses the email")
    })
    public ResponseEntity<?> findMemberCandidate(
            @PathVariable
            @Parameter(description = "Cooperative unique identifier", example = "1", required = true)
            Long cooperativeId,
            @RequestParam
            @Parameter(description = "Email of the producer account", example = "guillermo.quispe@gmail.com", required = true)
            String email,
            @AuthenticationPrincipal UserDetailsImpl principal) {
        var denied = requireDirector(principal);
        if (denied != null) return denied;
        var cooperative = cooperativeQueryService.handle(new GetCooperativeByIdQuery(cooperativeId))
                .filter(found -> found.getManagerUserId().equals(principal.getId()));
        if (cooperative.isEmpty()) {
            return notFound(cooperativeId);
        }
        var userId = externalIamService.findFarmerUserIdByEmail(email);
        var profile = userId.flatMap(id -> profileQueryService.handle(new GetProfileByUserIdQuery(id)));
        if (profile.isEmpty()) {
            var error = ApplicationError.notFound("ProducerAccount", email.trim().toLowerCase(java.util.Locale.ROOT));
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
        }
        var found = profile.get();
        return ResponseEntity.ok(new MemberCandidateResource(
                found.getUserId(),
                email.trim().toLowerCase(java.util.Locale.ROOT),
                found.getName().firstName(),
                found.getName().lastName(),
                found.getDni(),
                found.getPhoneNumber(),
                cooperative.get().hasActiveMemberWithUserId(found.getUserId())));
    }

    @PutMapping("/{cooperativeId}/members/{memberId}/account")
    @Operation(summary = "Link a member to his account",
            description = "Links a member registered without an account to the producer account that uses the given email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member linked",
                    content = @Content(schema = @Schema(implementation = CooperativeMemberResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email"),
            @ApiResponse(responseCode = "404", description = "Cooperative or member not found"),
            @ApiResponse(responseCode = "409", description = "The member already has an account"),
            @ApiResponse(responseCode = "422", description = "No producer account uses the email")
    })
    public ResponseEntity<?> linkMemberAccount(
            @PathVariable
            @Parameter(description = "Cooperative unique identifier", example = "1", required = true)
            Long cooperativeId,
            @PathVariable
            @Parameter(description = "Member unique identifier", example = "11", required = true)
            Long memberId,
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody LinkCooperativeMemberAccountResource resource) {
        var denied = requireDirector(principal);
        if (denied != null) return denied;
        var command = LinkCooperativeMemberAccountCommandFromResourceAssembler.toCommandFromResource(
                cooperativeId, principal.getId(), memberId, resource);
        var result = cooperativeCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, CooperativeMemberResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @GetMapping("/{cooperativeId}/invitations")
    @Operation(summary = "Get the agronomist invitations of a cooperative",
            description = "Pending and accepted invitations. The accepted ones carry the user id of the agronomist: "
                    + "they are the technical team the director can assign to the plots of the members.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitations retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AgronomistInvitationResource.class)))),
            @ApiResponse(responseCode = "404", description = "Cooperative not found")
    })
    public ResponseEntity<?> getInvitations(
            @PathVariable
            @Parameter(description = "Cooperative unique identifier", example = "1", required = true)
            Long cooperativeId,
            @AuthenticationPrincipal UserDetailsImpl principal) {
        var denied = requireDirector(principal);
        if (denied != null) return denied;
        var cooperative = cooperativeQueryService.handle(new GetCooperativeByIdQuery(cooperativeId))
                .filter(found -> found.getManagerUserId().equals(principal.getId()));
        if (cooperative.isEmpty()) {
            return notFound(cooperativeId);
        }
        return ResponseEntity.ok(cooperative.get().getInvitations().stream()
                .map(AgronomistInvitationResourceFromEntityAssembler::toResourceFromEntity)
                .toList());
    }

    @PostMapping("/{cooperativeId}/invitations")
    @Operation(summary = "Invite an agronomist",
            description = "Creates an invitation with a unique token for an agronomist (US-19). "
                    + "An agronomist who already has an account with that email joins the technical team at once.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Invitation created",
                    content = @Content(schema = @Schema(implementation = AgronomistInvitationResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email or email already invited"),
            @ApiResponse(responseCode = "404", description = "Cooperative not found")
    })
    public ResponseEntity<?> inviteAgronomist(
            @PathVariable
            @Parameter(description = "Cooperative unique identifier", example = "1", required = true)
            Long cooperativeId,
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody InviteAgronomistResource resource) {
        var denied = requireDirector(principal);
        if (denied != null) return denied;
        var command = InviteAgronomistCommandFromResourceAssembler.toCommandFromResource(
                cooperativeId, principal.getId(), resource);
        var result = cooperativeCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, AgronomistInvitationResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    /** Only a cooperative director manages members and invitations. */
    private static ResponseEntity<?> requireDirector(UserDetailsImpl principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (principal.getAuthorities().stream()
                .noneMatch(authority -> "ROLE_COOPERATIVE_DIRECTOR".equals(authority.getAuthority()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return null;
    }

    private ResponseEntity<?> notFound(Long cooperativeId) {
        var error = ApplicationError.notFound("Cooperative", cooperativeId.toString());
        return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
    }
}
