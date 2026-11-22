package com.dymbia.sumaqagro.platform.crop.application.internal.eventhandlers;

import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.acl.ExternalFarmingService;
import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.acl.ExternalProfilesService;
import com.dymbia.sumaqagro.platform.crop.domain.model.events.AgroclimaticAlertIssuedEvent;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.AgroclimaticAlertSeverity;
import com.dymbia.sumaqagro.platform.shared.application.outboundservices.messaging.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Set;

/**
 * Logs newly issued agroclimatic alerts and, when they are serious, warns the producers of the region
 * by WhatsApp or SMS (Twilio), at the phone of their profile.
 * <p>
 * It runs after the alert is stored, and a failing message never undoes the alert: the producer still
 * sees it in the application.
 * </p>
 */
@Service
public class AgroclimaticAlertIssuedEventHandler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AgroclimaticAlertIssuedEventHandler.class);
    // Only serious alerts reach the phone, so producers are not flooded with messages
    static final Set<AgroclimaticAlertSeverity> NOTIFIED_SEVERITIES =
            Set.of(AgroclimaticAlertSeverity.HIGH, AgroclimaticAlertSeverity.CRITICAL);

    private final ExternalFarmingService externalFarmingService;
    private final ExternalProfilesService externalProfilesService;
    private final MessageSender messageSender;

    public AgroclimaticAlertIssuedEventHandler(ExternalFarmingService externalFarmingService,
                                               ExternalProfilesService externalProfilesService,
                                               MessageSender messageSender) {
        this.externalFarmingService = externalFarmingService;
        this.externalProfilesService = externalProfilesService;
        this.messageSender = messageSender;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(AgroclimaticAlertIssuedEvent event) {
        LOGGER.info(
                "Agroclimatic alert issued: region={}, severity={}, issuedAt={}, source={}",
                event.getRegion(),
                event.getSeverity(),
                event.getIssuedAt(),
                event.getAlertSource()
        );
        if (!NOTIFIED_SEVERITIES.contains(event.getSeverity()) || !messageSender.isEnabled()) {
            return;
        }

        var body = messageBody(event);
        int sent = 0;
        for (var producerUserId : externalFarmingService.fetchProducerUserIdsByRegion(event.getRegion())) {
            var phone = externalProfilesService.fetchPhoneNumberByUserId(producerUserId);
            if (phone.isEmpty()) {
                continue;
            }
            try {
                messageSender.send(phone.get(), body);
                sent++;
            } catch (MessageSender.DeliveryFailed exception) {
                // The phone is not logged: it is personal data
                LOGGER.warn("The alert message for producer {} was not sent: {}", producerUserId,
                        exception.getMessage());
            }
        }
        LOGGER.info("Agroclimatic alert '{}' sent to {} producer(s) of {}", event.getTitle(), sent, event.getRegion());
    }

    static String messageBody(AgroclimaticAlertIssuedEvent event) {
        return "SumaqAgro - %s (%s, severidad %s)%n%s".formatted(
                event.getTitle(), event.getRegion(), event.getSeverity(), event.getDescription());
    }
}
