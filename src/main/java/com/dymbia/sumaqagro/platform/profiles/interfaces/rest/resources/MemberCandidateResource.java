package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MemberCandidateResponse",
        description = "Data of a producer account the director can add to the member list, read from its profile")
public record MemberCandidateResource(
        @Schema(description = "IAM user id of the producer", example = "9")
        Long userId,
        @Schema(description = "Account email", example = "guillermo.quispe@gmail.com")
        String email,
        @Schema(description = "First name", example = "Guillermo")
        String firstName,
        @Schema(description = "Last name", example = "Quispe")
        String lastName,
        @Schema(description = "DNI, when the producer added it to his profile", example = "45678912", nullable = true)
        String dni,
        @Schema(description = "Phone number, when the producer added it", example = "+51 987 654 321", nullable = true)
        String phoneNumber,
        @Schema(description = "Whether the producer is already a member of this cooperative", example = "false")
        boolean alreadyMember
) {
}
