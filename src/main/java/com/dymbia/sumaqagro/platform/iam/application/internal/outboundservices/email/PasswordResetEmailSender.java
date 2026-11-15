package com.dymbia.sumaqagro.platform.iam.application.internal.outboundservices.email;

import com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects.EmailAddress;

import java.net.URI;

/** Delivers password recovery links to IAM accounts. */
public interface PasswordResetEmailSender {

    /**
     * Sends a password recovery link to the account's email address.
     *
     * @param recipient the email address associated with the account
     * @param resetLink the recovery URL containing the single-use token
     */
    void send(EmailAddress recipient, URI resetLink);
}