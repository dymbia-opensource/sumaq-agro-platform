package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(
        name = "UpdateProfileRequest",
        description = "Request payload for updating a profile. photoUrl and preferredLanguage are optional; "
                + "when they are missing the current values are kept. Any 'email' sent is ignored (IAM owns it).",
        example = "{\"firstName\": \"Guillermo\", \"lastName\": \"Quispe\", \"phoneNumber\": \"+51 987 654 321\", \"preferredLanguage\": \"ES\"}"
)
public record UpdateProfileResource(
        @NotBlank(message = "{validation.not-blank}")
        @Size(max = 100)
        @Schema(description = "First name", example = "Guillermo", maxLength = 100)
        String firstName,

        @NotBlank(message = "{validation.not-blank}")
        @Size(max = 100)
        @Schema(description = "Last name", example = "Quispe", maxLength = 100)
        String lastName,

        @Size(max = 15)
        @Schema(description = "Phone number", example = "+51 987 654 321", maxLength = 15)
        String phoneNumber,

        @Size(max = 255)
        @Schema(description = "Profile photo URL (optional)", example = "https://cdn.sumaqagro.pe/photos/1.png")
        String photoUrl,

        @Pattern(regexp = "(?i)ES|EN", message = "{validation.language}")
        @Schema(description = "Interface language (optional)", example = "ES", allowableValues = {"ES", "EN"})
        String preferredLanguage
) {
}
