package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ProfileResponse", description = "Profile information response")
public record ProfileResource(
        @Schema(description = "Profile unique identifier", example = "1")
        Long id,
        @Schema(description = "IAM user id that owns the profile", example = "1")
        Long userId,
        @Schema(description = "First name", example = "Guillermo")
        String firstName,
        @Schema(description = "Last name", example = "Quispe")
        String lastName,
        @Schema(description = "Full name", example = "Guillermo Quispe")
        String fullName,
        @Schema(description = "Account email, read-only (owned by IAM)", example = "guillermo@sumaqagro.pe")
        String email,
        @Schema(description = "Phone number for SMS / WhatsApp alerts", example = "+51 987 654 321")
        String phoneNumber,
        @Schema(description = "Profile photo URL", example = "https://cdn.sumaqagro.pe/photos/1.png")
        String photoUrl,
        @Schema(description = "Interface language", example = "ES", allowableValues = {"ES", "EN"})
        String preferredLanguage
) {
}
