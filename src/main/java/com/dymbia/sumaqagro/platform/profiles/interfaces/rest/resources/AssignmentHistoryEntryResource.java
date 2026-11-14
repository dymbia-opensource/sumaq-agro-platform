package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(name = "AssignmentHistoryEntryResponse", description = "A previous agronomist of the plot")
public record AssignmentHistoryEntryResource(
        @Schema(description = "Previous agronomist user id (frontend: advisorUserId)", example = "4")
        Long agronomistUserId,
        @Schema(description = "When he was assigned", example = "2026-10-15T14:00:00Z")
        Instant assignedAt,
        @Schema(description = "When he stopped attending the plot", example = "2026-11-10T09:00:00Z")
        Instant endedAt
) {
}
