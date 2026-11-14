package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(
        name = "UpdateCooperativeRequest",
        description = "Request payload for updating the institutional data of a cooperative. All fields are required.",
        example = "{\"ruc\": \"20612345678\", \"businessName\": \"Cooperativa Agraria Valle del Mantaro\", \"region\": \"Junín\", "
                + "\"institutionalEmail\": \"contacto@valledelmantaro.pe\", \"legalRepresentative\": \"Cristian Santana\", "
                + "\"headquartersAddress\": \"Av. Ferrocarril 123, Huancayo\", \"switchboardPhone\": \"+51 64 555 200\"}"
)
public record UpdateCooperativeResource(
        @NotBlank(message = "{validation.not-blank}")
        @Pattern(regexp = "\\d{11}", message = "{validation.ruc}")
        @Schema(description = "RUC (11 digits)", example = "20612345678")
        String ruc,

        @NotBlank(message = "{validation.not-blank}")
        @Size(max = 150)
        @Schema(description = "Business name", example = "Cooperativa Agraria Valle del Mantaro")
        String businessName,

        @NotBlank(message = "{validation.not-blank}")
        @Size(max = 100)
        @Schema(description = "Region", example = "Junín - Valle del Mantaro")
        String region,

        @NotBlank(message = "{validation.not-blank}")
        @Email(message = "{validation.email}")
        @Size(max = 150)
        @Schema(description = "Institutional email", example = "contacto@valledelmantaro.pe")
        String institutionalEmail,

        @NotBlank(message = "{validation.not-blank}")
        @Size(max = 150)
        @Schema(description = "Legal representative", example = "Cristian Santana")
        String legalRepresentative,

        @NotBlank(message = "{validation.not-blank}")
        @Size(max = 255)
        @Schema(description = "Headquarters address", example = "Av. Ferrocarril 123, Huancayo")
        String headquartersAddress,

        @NotBlank(message = "{validation.not-blank}")
        @Size(max = 15)
        @Schema(description = "Switchboard phone", example = "+51 64 555 200")
        String switchboardPhone
) {
}
