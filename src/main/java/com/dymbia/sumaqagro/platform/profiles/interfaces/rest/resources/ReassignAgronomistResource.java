package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(
        name = "ReassignAgronomistRequest",
        description = "Request payload for giving the plot to another agronomist. The previous one goes to the history.",
        example = "{\"agronomistUserId\": 5}"
)
public record ReassignAgronomistResource(
        @NotNull
        @Positive
        @Schema(description = "New agronomist user id", example = "5")
        Long agronomistUserId
) {
}
