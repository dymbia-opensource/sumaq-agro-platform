package com.dymbia.sumaqagro.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Represents a request to create an IAM account and its initial profile.
 *
 * @param email the email used to sign in
 * @param password the raw password
 * @param roles the single role requested during registration
 * @param profile the initial profile data
 */
@Schema(name = "SignUpRequest", description = "Account and initial profile data")
public record SignUpResource(
        @NotBlank @Email @Size(max = 254)
        String email,

        @NotBlank @Size(min = 8, max = 72)
        @Schema(accessMode = Schema.AccessMode.WRITE_ONLY)
        String password,

        @NotNull @Size(min = 1, max = 1)
        List<@NotNull @Pattern(
                regexp = "ROLE_FARMER|ROLE_COOPERATIVE_DIRECTOR|ROLE_AGRONOMIST"
        ) String> roles,

        @NotNull @Valid
        ProfileResource profile
) {

    /**
     * Contains the names required to create the user's profile.
     *
     * @param firstName the user's given names
     * @param lastName the user's family names
     */
    public record ProfileResource(
            @NotBlank @Size(max = 100) String firstName,
            @NotBlank @Size(max = 100) String lastName
    ) {
    }

    /**
     * Returns the resource name without exposing its credentials.
     *
     * @return the resource name
     */
    @Override
    public String toString() {
        return "SignUpResource";
    }
}