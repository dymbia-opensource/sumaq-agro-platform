package com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects;

/**
 * Peruvian RUC value object (11 digits).
 *
 * @param value RUC as a string of exactly 11 digits
 * @since 1.0
 */
public record Ruc(String value) {

    private static final int REQUIRED_LENGTH = 11;

    public Ruc {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("RUC must not be null or blank");
        }
        value = value.trim();
        if (value.length() != REQUIRED_LENGTH || !value.matches("\\d{11}")) {
            throw new IllegalArgumentException("RUC must be exactly 11 digits");
        }
    }
}