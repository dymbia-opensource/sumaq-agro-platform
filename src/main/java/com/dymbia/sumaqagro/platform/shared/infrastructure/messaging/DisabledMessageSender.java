package com.dymbia.sumaqagro.platform.shared.infrastructure.messaging;

import com.dymbia.sumaqagro.platform.shared.application.outboundservices.messaging.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * {@link MessageSender} used when there are no Twilio credentials ({@code TWILIO_ACCOUNT_SID} is empty).
 * It sends nothing, so the application starts without Twilio.
 *
 * @since 1.1
 */
@Component
@ConditionalOnExpression("'${notifications.twilio.account-sid:}'.isBlank()")
public class DisabledMessageSender implements MessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(DisabledMessageSender.class);

    @Override
    public void send(String phoneNumber, String body) {
        // The phone is not logged: it is personal data
        LOGGER.info("Messaging is disabled (no TWILIO_ACCOUNT_SID); a notification message was not sent");
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
