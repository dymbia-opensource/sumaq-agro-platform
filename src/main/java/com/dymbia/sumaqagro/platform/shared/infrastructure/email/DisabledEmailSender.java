package com.dymbia.sumaqagro.platform.shared.infrastructure.email;

import com.dymbia.sumaqagro.platform.shared.application.outboundservices.email.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * {@link EmailSender} used when email notifications are turned off ({@code notifications.email.enabled=false}).
 * It sends nothing, so the application starts without Brevo credentials.
 *
 * @since 1.1
 */
@Component
@ConditionalOnProperty(name = "notifications.email.enabled", havingValue = "false", matchIfMissing = true)
public class DisabledEmailSender implements EmailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(DisabledEmailSender.class);

    @Override
    public void send(String recipientEmail, String subject, String htmlContent) {
        // The recipient is not logged: it is personal data
        LOGGER.info("Email notifications are disabled; the email '{}' was not sent", subject);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
