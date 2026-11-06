package com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects;

/**
 * Person name value object (first name + last name).
 *
 * @param firstName person's first name, not blank
 * @param lastName  person's last name, not blank
 * @since 1.0
 */
public record PersonName(String firstName, String lastName) {

    private static final int MAX_LENGTH = 100;

    public PersonName {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name must not be null or blank");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name must not be null or blank");
        }
        firstName = firstName.trim();
        lastName = lastName.trim();
        if (firstName.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("First name must not exceed " + MAX_LENGTH + " characters");
        }
        if (lastName.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Last name must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public String fullName() {
        return firstName + " " + lastName;
    }
}