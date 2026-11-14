package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(
        name = "RegisterCooperativeRequest",
        description = "Request payload for registering a cooperative (US-17). The last four fields are optional.",
        example = "{\"ruc\": \"20612345678\", \"businessName\": \"Cooperativa Agraria Valle del Mantaro\", \"region\": \"Junín\", \"managerUserId\": 3}"
)
public record RegisterCooperativeResource(
        @NotBlank(message = "{validation.not-blank}")
        @Pattern(regexp = "\\d{11}", message = "{validation.ruc}")
        @Schema(description = "RUC (11 digits)", example = "20612345678")
        String ruc,

        @NotBlank(message = "{validation.not-blank}")
        @Size(max = 150)
        @Schema(description = "Business name", example = "Cooperativa Agraria Valle del Mantaro", maxLength = 150)
        String businessName,

        @NotBlank(message = "{validation.not-blank}")
        @Size(max = 100)
        @Schema(description = "Region", example = "Junín - Valle del Mantaro", maxLength = 100)
        String region,

        @NotNull
        @Positive
        @Schema(description = "IAM user id of the director", example = "3")
        Long managerUserId,

        @Email(message = "{validation.email}")
        @Size(max = 150)
        @Schema(description = "Institutional email (optional)", example = "contacto@valledelmantaro.pe")
        String institutionalEmail,

        @Size(max = 150)
        @Schema(description = "Legal representative (optional)", example = "Cristian Santana")
        String legalRepresentative,

        @Size(max = 255)
        @Schema(description = "Headquarters address (optional)", example = "Av. Ferrocarril 123, Huancayo")
        String headquartersAddress,

        @Size(max = 15)
        @Schema(description = "Switchboard phone (optional)", example = "+51 64 555 200")
        String switchboardPhone
) {
}
