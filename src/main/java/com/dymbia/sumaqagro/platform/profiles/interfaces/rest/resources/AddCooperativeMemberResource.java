package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(
        name = "AddCooperativeMemberRequest",
        description = "Request payload for adding a member to a cooperative (US-18). joinedAt is optional; "
                + "email links the member to the producer account that uses it (send it or userId, not both).",
        example = "{\"dni\": \"45678912\", \"firstName\": \"Guillermo\", \"lastName\": \"Quispe\", \"community\": \"Sicaya\", \"joinedAt\": \"2024-03-15\", \"email\": \"guillermo.quispe@gmail.com\"}"
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
        @Schema(description = "IAM user id of the member (optional, deprecated: send email)", example = "1", nullable = true)
        Long userId,
        @Email(message = "{validation.email}")
        @Size(max = 150)
        @Schema(description = "Email of the producer account of the member (optional)",
                example = "guillermo.quispe@gmail.com", nullable = true)
        String email
) {
}
