package com.dymbia.sumaqagro.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Input for setting a new password with a recovery token. */
public record ResetPasswordResource(
        @NotBlank @Size(max = 43) String token,
        @NotBlank @Size(max = 72) String newPassword
) {
    @Override
    public String toString() {
        return "ResetPasswordResource";
    }
}