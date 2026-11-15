package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(name = "AgronomistInvitationResponse", description = "Invitation sent to an agronomist")
public record AgronomistInvitationResource(
        @Schema(description = "Invitation unique identifier", example = "1")
        Long id,
        @Schema(description = "Cooperative id", example = "1")
        Long cooperativeId,
        @Schema(description = "Email of the agronomist", example = "juan.morales@agro.pe")
        String email,
        @Schema(description = "Invitation status", example = "PENDING", allowableValues = {"PENDING", "ACCEPTED", "EXPIRED"})
        String status,
        @Schema(description = "When the invitation was sent", example = "2026-11-13T15:00:00Z")
        Instant sentAt
) {
}
