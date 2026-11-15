package com.dymbia.sumaqagro.platform.iam.domain.model.commands;

import com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects.EmailAddress;

import java.util.Objects;

/**
 * Requests a password reset for an email address.
 */
public record RequestPasswordResetCommand(EmailAddress email) {
    public RequestPasswordResetCommand {
        Objects.requireNonNull(email, "Email is required");
    }
}