package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(
        name = "AssignAgronomistToPlotRequest",
        description = "Request payload used by the cooperative director to assign an agronomist to a plot (US-20). "
                + "producerUserId is not sent: it is read from Field Management.",
        example = "{\"cooperativeId\": 1, \"agronomistUserId\": 4, \"plotId\": 1}"
)
public record AssignAgronomistToPlotResource(
        @NotNull
        @Positive
        @Schema(description = "Cooperative id", example = "1")
        Long cooperativeId,

        @NotNull
        @Positive
        @Schema(description = "Agronomist user id", example = "4")
        Long agronomistUserId,

        @NotNull
        @Positive
        @Schema(description = "Plot id (Field Management)", example = "1")
        Long plotId
) {
}
