package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CooperativeResponse", description = "Cooperative institutional data")
public record CooperativeResource(
        @Schema(description = "Cooperative unique identifier", example = "1")
        Long id,
        @Schema(description = "RUC (11 digits)", example = "20612345678")
        String ruc,
        @Schema(description = "Business name (frontend: legalName)", example = "Cooperativa Agraria Valle del Mantaro")
        String businessName,
        @Schema(description = "Region", example = "Junín - Valle del Mantaro")
        String region,
        @Schema(description = "IAM user id of the director (frontend: directorUserId)", example = "3")
        Long managerUserId,
        @Schema(description = "Institutional email", example = "contacto@valledelmantaro.pe")
        String institutionalEmail,
        @Schema(description = "Legal representative", example = "Cristian Santana")
        String legalRepresentative,
        @Schema(description = "Headquarters address", example = "Av. Ferrocarril 123, Huancayo")
        String headquartersAddress,
        @Schema(description = "Switchboard phone", example = "+51 64 555 200")
        String switchboardPhone
) {
}
