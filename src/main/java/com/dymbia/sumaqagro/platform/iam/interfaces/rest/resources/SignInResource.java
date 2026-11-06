package com.dymbia.sumaqagro.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents the credentials received by the token endpoint.
 *
 * @param email the account email
 * @param password the raw password
 */
@Schema(name = "SignInRequest", description = "Credentials used to sign in")
public record SignInResource(
        @NotBlank
        @Email
        @Size(max = 254)
        @Schema(example = "ana@example.com")
        String email,

        @NotBlank
        @Schema(accessMode = Schema.AccessMode.WRITE_ONLY)
        String password
) {

    /**
     * Returns a representation safe for logging.
     *
     * @return the resource name without its credentials
     */
    @Override
    public String toString() {
        return "SignInResource";
    }
}