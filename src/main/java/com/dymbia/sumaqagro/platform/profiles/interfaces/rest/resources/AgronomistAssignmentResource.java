package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(name = "AgronomistAssignmentResponse", description = "Agronomist assigned to a plot")
public record AgronomistAssignmentResource(
        @Schema(description = "Assignment unique identifier", example = "1")
        Long id,
        @Schema(description = "Cooperative that made the assignment", example = "1")
        Long cooperativeId,
        @Schema(description = "Agronomist user id (frontend: advisorUserId)", example = "4")
        Long agronomistUserId,
        @Schema(description = "Plot id (Field Management)", example = "1")
        Long plotId,
        @Schema(description = "Owner of the plot", example = "1")
        Long producerUserId,
        @Schema(description = "When the current agronomist was assigned", example = "2026-10-15T14:00:00Z")
        Instant assignedAt,
        @Schema(description = "Whether the assignment is active", example = "true")
        boolean active,
        @Schema(description = "Same as 'active', in the format the frontend uses", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
        String status,
        @Schema(description = "Previous agronomists of the plot")
        List<AssignmentHistoryEntryResource> history
) {
}
