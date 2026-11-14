package com.dymbia.sumaqagro.platform.iam.interfaces.rest;

import com.dymbia.sumaqagro.platform.iam.application.commandservices.UserCommandService;
import com.dymbia.sumaqagro.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.dymbia.sumaqagro.platform.iam.interfaces.rest.resources.SignInResource;
import com.dymbia.sumaqagro.platform.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.profiles.interfaces.acl.ProfilesContextFacade;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes the IAM endpoint for issuing access tokens.
 */
@RestController
@RequestMapping(value = "/api/v1/tokens", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Tokens", description = "Authentication token endpoints")
public class TokensController {

    private final UserCommandService userCommandService;

    private final ProfilesContextFacade profilesContextFacade;

    public TokensController(UserCommandService userCommandService, ProfilesContextFacade profilesContextFacade) {
        this.userCommandService = userCommandService;
        this.profilesContextFacade = profilesContextFacade;
    }

    /**
     * Authenticates the supplied credentials and returns an access token.
     *
     * @param signInResource the sign-in request
     * @return the authentication response or an HTTP error
     */
    @PostMapping
    @Operation(summary = "Create authentication token")
    @ApiResponse(
            responseCode = "200",
            description = "Token issued successfully",
            content = @Content(schema = @Schema(implementation = AuthenticatedUserResource.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    @ApiResponse(responseCode = "401", description = "Invalid email or password")
    public ResponseEntity<?> generateToken(@Valid @RequestBody SignInResource signInResource) {
        var command = SignInCommandFromResourceAssembler.toCommandFromResource(signInResource);
        var result = userCommandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                auth -> AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(
                        auth.getLeft(), auth.getRight(),
                        profilesContextFacade.fetchFullNameByUserId(auth.getLeft().getId()).orElse(null)
                ),
                HttpStatus.OK
        );
    }
}