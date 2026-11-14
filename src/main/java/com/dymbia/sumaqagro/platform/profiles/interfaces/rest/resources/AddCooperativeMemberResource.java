package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(
        name = "AddCooperativeMemberRequest",
        description = "Request payload for adding a member to a cooperative (US-18). joinedAt and userId are optional.",
        example = "{\"dni\": \"45678912\", \"firstName\": \"Guillermo\", \"lastName\": \"Quispe\", \"community\": \"Sicaya\", \"joinedAt\": \"2024-03-15\", \"userId\": 1}"
)
public record AddCooperativeMemberResource(
        @NotBlank(message = "{validation.not-blank}")
        @Pattern(regexp = "\\d{8}", message = "{validation.dni}")
        @Schema(description = "DNI (8 digits)", example = "45678912")
        String dni,

        @NotBlank(message = "{validation.not-blank}")
        @Size(max = 100)
        @Schema(description = "First name", example = "Guillermo")
        String firstName,

        @NotBlank(message = "{validation.not-blank}")
        @Size(max = 100)
        @Schema(description = "Last name", example = "Quispe")
        String lastName,

        @NotBlank(message = "{validation.not-blank}")
        @Size(max = 100)
        @Schema(description = "Community or hamlet", example = "Sicaya")
        String community,

        @Schema(description = "Date the member joined (optional, today by default)", example = "2024-03-15")
        LocalDate joinedAt,

        @Positive
        @Schema(description = "IAM user id of the member (optional)", example = "1", nullable = true)
        Long userId
) {
}
