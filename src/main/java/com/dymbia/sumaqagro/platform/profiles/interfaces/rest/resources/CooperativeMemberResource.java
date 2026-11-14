package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "CooperativeMemberResponse", description = "Member of a cooperative")
public record CooperativeMemberResource(
        @Schema(description = "Member unique identifier", example = "11")
        Long id,
        @Schema(description = "Cooperative id", example = "1")
        Long cooperativeId,
        @Schema(description = "DNI (8 digits)", example = "45678912")
        String dni,
        @Schema(description = "First name", example = "Guillermo")
        String firstName,
        @Schema(description = "Last name", example = "Quispe")
        String lastName,
        @Schema(description = "Full name", example = "Guillermo Quispe")
        String fullName,
        @Schema(description = "Community or hamlet", example = "Sicaya")
        String community,
        @Schema(description = "Date the member joined", example = "2024-03-15")
        LocalDate joinedAt,
        @Schema(description = "Whether the member is active", example = "true")
        boolean active,
        @Schema(description = "IAM user id of the member, if he has an account", example = "1", nullable = true)
        Long userId
) {
}
