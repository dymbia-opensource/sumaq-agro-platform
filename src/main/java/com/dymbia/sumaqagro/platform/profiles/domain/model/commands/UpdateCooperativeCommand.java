package com.dymbia.sumaqagro.platform.profiles.domain.model.commands;

/**
 * Command to update the institutional data of a cooperative. All fields are required,
 * as in the cooperative settings view of the frontend.
 *
 * @since 1.0
 */
public record UpdateCooperativeCommand(
        Long cooperativeId,
        String ruc,
        String businessName,
        String region,
        String institutionalEmail,
        String legalRepresentative,
        String headquartersAddress,
        String switchboardPhone
) {
    public UpdateCooperativeCommand {
        if (cooperativeId == null || cooperativeId <= 0) {
            throw new IllegalArgumentException("Cooperative id must be a positive number");
        }
        requireText(ruc, "RUC");
        requireText(businessName, "Business name");
        requireText(region, "Region");
        requireText(institutionalEmail, "Institutional email");
        requireText(legalRepresentative, "Legal representative");
        requireText(headquartersAddress, "Headquarters address");
        requireText(switchboardPhone, "Switchboard phone");
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be null or blank");
        }
    }
}
