package com.dymbia.sumaqagro.platform.profiles.interfaces.rest;

import com.dymbia.sumaqagro.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.dymbia.sumaqagro.platform.profiles.application.commandservices.ProfileCommandService;
import com.dymbia.sumaqagro.platform.profiles.application.internal.outboundservices.acl.ExternalIamService;
import com.dymbia.sumaqagro.platform.profiles.application.queryservices.ProfileQueryService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetProfileByIdQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.ProfileResource;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.UpdateProfileResource;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform.ProfileResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform.UpdateProfileCommandFromResourceAssembler;
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
@RequestMapping(value = "/api/v1/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Profiles", description = "User profile endpoints")
public class ProfilesController {

    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;
    private final ExternalIamService externalIamService;

    public ProfilesController(ProfileCommandService profileCommandService,
                              ProfileQueryService profileQueryService,
                              ExternalIamService externalIamService) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
        this.externalIamService = externalIamService;
    }

    @GetMapping("/{profileId}")
    @Operation(summary = "Get profile by ID", description = "Retrieves a user profile by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile found",
                    content = @Content(schema = @Schema(implementation = ProfileResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid profile id"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<?> getProfileById(
            @PathVariable
            @Parameter(description = "Profile unique identifier", example = "1", required = true)
            Long profileId,
            @AuthenticationPrincipal UserDetailsImpl principal) {
        var profile = profileQueryService.handle(new GetProfileByIdQuery(profileId));
        if (profile.isEmpty()) {
            var error = ApplicationError.notFound("Profile", profileId.toString());
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
        }
        return ResponseEntity.ok(toResource(profile.get(), principal));
    }

    @GetMapping
    @Operation(summary = "Get profiles",
            description = "Returns the profile of the user given in 'userId', or the caller's own profile when it is not sent. "
                    + "Always returns a list. The phone, email and DNI are only shown to the owner; other users only see the name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profiles retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProfileResource.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid user id")
    })
    public ResponseEntity<List<ProfileResource>> getProfiles(
            @RequestParam(required = false)
            @Parameter(description = "IAM user id (optional filter)", example = "1")
            Long userId,
            @AuthenticationPrincipal UserDetailsImpl principal) {
        // The whole directory of users is never listed: only one profile, the caller's own by default
        var requested = userId != null ? userId : (principal != null ? principal.getId() : null);
        List<Profile> profiles = requested == null
                ? List.of()
                : profileQueryService.handle(new GetProfileByUserIdQuery(requested)).map(List::of).orElse(List.of());
        return ResponseEntity.ok(profiles.stream().map(profile -> toResource(profile, principal)).toList());
    }

    @PatchMapping("/{profileId}")
    @Operation(summary = "Update profile",
            description = "Updates name, phone, photo and language of a profile (US-16). photoUrl and preferredLanguage are optional: when they are missing the current values are kept. The email belongs to IAM and is not changed here.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated",
                    content = @Content(schema = @Schema(implementation = ProfileResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "The profile belongs to another user"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<?> updateProfile(
            @PathVariable
            @Parameter(description = "Profile unique identifier", example = "1", required = true)
            Long profileId,
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody UpdateProfileResource resource) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        var current = profileQueryService.handle(new GetProfileByIdQuery(profileId));
        if (current.isPresent() && !current.get().getUserId().equals(principal.getId())) {
            // Each user edits only his own profile
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        var command = UpdateProfileCommandFromResourceAssembler.toCommandFromResource(profileId, resource);
        var result = profileCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, profile -> toResource(profile, principal), HttpStatus.OK);
    }

    private ProfileResource toResource(Profile profile, UserDetailsImpl principal) {
        var owner = principal != null && profile.getUserId().equals(principal.getId());
        // The email is only read from IAM for the owner, the only one who sees it
        var email = owner ? externalIamService.fetchEmailByUserId(profile.getUserId()).orElse(null) : null;
        return ProfileResourceFromEntityAssembler.toResourceFromEntity(profile, email, owner);
    }
}
