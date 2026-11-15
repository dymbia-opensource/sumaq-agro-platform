package com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects;

/**
 * Peruvian DNI value object (8 digits).
 *
 * @param value DNI as a string of exactly 8 digits
 * @since 1.0
 */
public record Dni(String value) {

    private static final int REQUIRED_LENGTH = 8;

    public Dni {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("DNI must not be null or blank");
        }
        value = value.trim();
        if (value.length() != REQUIRED_LENGTH || !value.matches("\\d{8}")) {
            throw new IllegalArgumentException("DNI must be exactly 8 digits");
        }
    }
}