package com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects;

/**
 * Phone number value object.
 * Accepts numbers with optional country code (e.g. +51 999888777).
 *
 * @param value phone number, not blank and max 15 characters
 * @since 1.0
 */
public record PhoneNumber(String value) {

    private static final int MAX_LENGTH = 15;

    public PhoneNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Phone number must not be null or blank");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Phone number must not exceed " + MAX_LENGTH + " characters");
        }
    }
}