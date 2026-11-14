package com.dymbia.sumaqagro.platform.iam.interfaces.rest;

import com.dymbia.sumaqagro.platform.iam.application.commandservices.UserCommandService;
import com.dymbia.sumaqagro.platform.iam.interfaces.rest.resources.SignUpResource;
import com.dymbia.sumaqagro.platform.iam.interfaces.rest.resources.UserResource;
import com.dymbia.sumaqagro.platform.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
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
 * Exposes the IAM endpoint for registering user accounts.
 */
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "User registration endpoints")
public class UsersController {

    private final UserCommandService userCommandService;

    public UsersController(UserCommandService userCommandService) {
        this.userCommandService = userCommandService;
    }

    /**
     * Registers a new user and creates the initial profile.
     *
     * @param signUpResource the registration request
     * @return the created account or an HTTP error
     */
    @PostMapping
    @Operation(summary = "Create user account")
    @ApiResponse(
            responseCode = "201",
            description = "Account and profile created",
            content = @Content(schema = @Schema(implementation = UserResource.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid registration data")
    @ApiResponse(responseCode = "409", description = "Email already registered")
    public ResponseEntity<?> createUser(@Valid @RequestBody SignUpResource signUpResource) {
        var command = SignUpCommandFromResourceAssembler.toCommandFromResource(signUpResource);
        var result = userCommandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                UserResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }
}