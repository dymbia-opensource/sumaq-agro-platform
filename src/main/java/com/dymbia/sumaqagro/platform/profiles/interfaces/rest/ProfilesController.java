package com.dymbia.sumaqagro.platform.profiles.interfaces.rest;

import com.dymbia.sumaqagro.platform.profiles.application.commandservices.ProfileCommandService;
import com.dymbia.sumaqagro.platform.profiles.application.internal.outboundservices.acl.ExternalIamService;
import com.dymbia.sumaqagro.platform.profiles.application.queryservices.ProfileQueryService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetAllProfilesQuery;
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
            Long profileId) {
        var profile = profileQueryService.handle(new GetProfileByIdQuery(profileId));
        if (profile.isEmpty()) {
            var error = ApplicationError.notFound("Profile", profileId.toString());
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
        }
        return ResponseEntity.ok(toResource(profile.get()));
    }

    @GetMapping
    @Operation(summary = "Get profiles",
            description = "Retrieves all profiles, or the profile of one user when 'userId' is sent. Always returns a list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profiles retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProfileResource.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid user id")
    })
    public ResponseEntity<List<ProfileResource>> getProfiles(
            @RequestParam(required = false)
            @Parameter(description = "IAM user id (optional filter)", example = "1")
            Long userId) {
        List<Profile> profiles = userId != null
                ? profileQueryService.handle(new GetProfileByUserIdQuery(userId)).map(profile -> List.of(profile)).orElse(List.of())
                : profileQueryService.handle(new GetAllProfilesQuery());
        return ResponseEntity.ok(profiles.stream().map(this::toResource).toList());
    }

    @PutMapping("/{profileId}")
    @Operation(summary = "Update profile",
            description = "Updates name, phone, photo and language of a profile (US-16). The email belongs to IAM and is not changed here.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated",
                    content = @Content(schema = @Schema(implementation = ProfileResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<?> updateProfile(
            @PathVariable
            @Parameter(description = "Profile unique identifier", example = "1", required = true)
            Long profileId,
            @Valid @RequestBody UpdateProfileResource resource) {
        var command = UpdateProfileCommandFromResourceAssembler.toCommandFromResource(profileId, resource);
        var result = profileCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(result, this::toResource, HttpStatus.OK);
    }

    private ProfileResource toResource(Profile profile) {
        var email = externalIamService.fetchEmailByUserId(profile.getUserId()).orElse(null);
        return ProfileResourceFromEntityAssembler.toResourceFromEntity(profile, email);
    }
}
