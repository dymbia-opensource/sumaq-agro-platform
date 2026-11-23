package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        name = "LinkCooperativeMemberAccountRequest",
        description = "Email of the producer account a member signs in with",
        example = "{\"email\": \"guillermo.quispe@gmail.com\"}"
)
public record LinkCooperativeMemberAccountResource(
        @NotBlank(message = "{validation.not-blank}")
        @Email(message = "{validation.email}")
        @Size(max = 150)
        @Schema(description = "Email of the producer account", example = "guillermo.quispe@gmail.com")
        String email
) {
}
