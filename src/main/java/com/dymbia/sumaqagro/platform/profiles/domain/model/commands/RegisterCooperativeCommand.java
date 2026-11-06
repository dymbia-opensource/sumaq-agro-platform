package com.dymbia.sumaqagro.platform.profiles.domain.model.commands;

/**
 * Command to register a new cooperative.
 *
 * @param ruc            RUC of the cooperative (11 digits)
 * @param businessName   legal name of the cooperative
 * @param region         region where the cooperative operates
 * @param managerUserId  user id of the cooperative manager
 * @since 1.0
 */
public record RegisterCooperativeCommand(
        String ruc,
        String businessName,
        String region,
        Long managerUserId
) {
    public RegisterCooperativeCommand {
        if (ruc == null || ruc.isBlank()) {
            throw new IllegalArgumentException("RUC must not be null or blank");
        }
        if (businessName == null || businessName.isBlank()) {
            throw new IllegalArgumentException("Business name must not be null or blank");
        }
        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException("Region must not be null or blank");
        }
        if (managerUserId == null || managerUserId <= 0) {
            throw new IllegalArgumentException("Manager user id must be a positive number");
        }
    }
}