package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        name = "InviteAgronomistRequest",
        description = "Request payload for inviting an agronomist by email (US-19)",
        example = "{\"email\": \"juan.morales@agro.pe\"}"
)
public record InviteAgronomistResource(
        @NotBlank(message = "{validation.not-blank}")
        @Email(message = "{validation.email}")
        @Size(max = 150)
        @Schema(description = "Email of the agronomist", example = "juan.morales@agro.pe")
        String email
) {
}
