package com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Email address value object.
 *
 * <p>Validates the format once, so every email used in the Profiles context
 * (institutional email, agronomist invitation) follows the same rule.</p>
 *
 * @param address the email address, stored trimmed and in lower case
 * @since 1.0
 */
public record EmailAddress(String address) {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final int MAX_LENGTH = 150;

    public EmailAddress {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Email address must not be null or blank");
        }
        address = address.trim().toLowerCase(Locale.ROOT);
        if (address.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Email address must not exceed " + MAX_LENGTH + " characters");
        }
        if (!EMAIL_PATTERN.matcher(address).matches()) {
            throw new IllegalArgumentException("Email address must be a valid format");
        }
    }
}
