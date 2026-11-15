package com.dymbia.sumaqagro.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Input for requesting a password recovery link. */
public record RequestPasswordResetResource(
        @NotBlank @Email @Size(max = 254) String email
) {
}