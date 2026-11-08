package com.dymbia.sumaqagro.platform.profiles.interfaces.rest;

import com.dymbia.sumaqagro.platform.profiles.application.commandservices.ProfileCommandService;
import com.dymbia.sumaqagro.platform.profiles.application.queryservices.ProfilesQueryService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.ProfileResource;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.UpdateProfileResource;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform.ProfileResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform.UpdateProfileCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Profile operations.
 *
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/api/v1/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Profiles", description = "User profile endpoints")

public class ProfilesController {
    private final ProfileCommandService profileCommandService;
    private final ProfilesQueryService profilesQueryService;

    public ProfilesController(ProfileCommandService profileCommandService,
                              ProfilesQueryService profilesQueryService) {
        this.profileCommandService = profileCommandService;
        this.profilesQueryService = profilesQueryService;
    }

    @Operation(summary = "Get profile by user id")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ProfileResource> getProfileByUserId(@PathVariable Long userId) {
        return profilesQueryService.handle(new GetProfileByUserIdQuery(userId))
                .map(profile -> ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(profile)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update a user profile")
    @PutMapping("/user/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId,
                                           @RequestBody UpdateProfileResource resource) {
        var command = UpdateProfileCommandFromResourceAssembler.toCommandFromResource(userId, resource);
        Result<?, ApplicationError> result = profileCommandService.handle(command);

        if (result.isSuccess()) {
            var profile = ((Result.Success<?, ApplicationError>) result).value();
            return ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(
                    (com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile) profile));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
