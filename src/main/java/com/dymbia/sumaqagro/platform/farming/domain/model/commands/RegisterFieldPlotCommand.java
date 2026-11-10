package com.dymbia.sumaqagro.platform.farming.domain.model.commands;

import java.math.BigDecimal;

/**
 * Command to register a new field plot for a producer.
 *
 * @param ownerUserId          the producer that owns the plot
 * @param name                 the plot name
 * @param region               the region or valley of the plot
 * @param declaredAreaHectares the area the producer declares, zero or positive
 * @since 1.0
 */
public record RegisterFieldPlotCommand(Long ownerUserId, String name, String region, BigDecimal declaredAreaHectares) {
    /**
     * Validates the command parameters. The messages are keys of {@code messages.properties}.
     *
     * @throws IllegalArgumentException when a parameter is invalid
     */
    public RegisterFieldPlotCommand {
        if (ownerUserId == null || ownerUserId <= 0) {
            throw new IllegalArgumentException("field.plot.error.ownerUserId.invalid");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("field.plot.error.name.notBlank");
        }
        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException("field.plot.error.region.notBlank");
        }
        if (declaredAreaHectares == null || declaredAreaHectares.signum() < 0) {
            throw new IllegalArgumentException("field.plot.error.area.invalid");
        }
    }
}
