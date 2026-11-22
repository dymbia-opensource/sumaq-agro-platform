package com.dymbia.sumaqagro.platform.crop.application.internal.eventhandlers;

import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.acl.ExternalFarmingService;
import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.acl.ExternalIamService;
import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.acl.ExternalProfilesService;
import com.dymbia.sumaqagro.platform.crop.domain.model.events.AgroclimaticAlertIssuedEvent;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.AgroclimaticAlertSeverity;
import com.dymbia.sumaqagro.platform.shared.application.outboundservices.email.EmailSender;
import com.dymbia.sumaqagro.platform.shared.application.outboundservices.messaging.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.util.HtmlUtils;

import java.util.Set;

/**
 * Logs newly issued agroclimatic alerts and, when they are serious, warns the producers of the region:
 * by email (Brevo) to the address of their account, and by WhatsApp or SMS (Twilio) to the phone of
 * their profile. Each channel only works when it is configured.
 * <p>
 * It runs after the alert is stored, and a failing notification never undoes the alert: the producer still
 * sees it in the application.
 * </p>
 */
@Service
public class AgroclimaticAlertIssuedEventHandler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AgroclimaticAlertIssuedEventHandler.class);
    // Only serious alerts reach the producer, so they are not flooded with notifications
    static final Set<AgroclimaticAlertSeverity> NOTIFIED_SEVERITIES =
            Set.of(AgroclimaticAlertSeverity.HIGH, AgroclimaticAlertSeverity.CRITICAL);

    private final ExternalFarmingService externalFarmingService;
    private final ExternalProfilesService externalProfilesService;
    private final ExternalIamService externalIamService;
    private final MessageSender messageSender;
    private final EmailSender emailSender;

    public AgroclimaticAlertIssuedEventHandler(ExternalFarmingService externalFarmingService,
                                               ExternalProfilesService externalProfilesService,
                                               ExternalIamService externalIamService,
                                               MessageSender messageSender,
                                               EmailSender emailSender) {
        this.externalFarmingService = externalFarmingService;
        this.externalProfilesService = externalProfilesService;
        this.externalIamService = externalIamService;
        this.messageSender = messageSender;
        this.emailSender = emailSender;
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
        var byEmail = emailSender.isEnabled();
        var byMessage = messageSender.isEnabled();
        if (!NOTIFIED_SEVERITIES.contains(event.getSeverity()) || (!byEmail && !byMessage)) {
            return;
        }

        int emails = 0;
        int messages = 0;
        for (var producerUserId : externalFarmingService.fetchProducerUserIdsByRegion(event.getRegion())) {
            if (byEmail && notifyByEmail(producerUserId, event)) {
                emails++;
            }
            if (byMessage && notifyByMessage(producerUserId, event)) {
                messages++;
            }
        }
        LOGGER.info("Agroclimatic alert '{}' of {} sent by {} email(s) and {} message(s)",
                event.getTitle(), event.getRegion(), emails, messages);
    }

    private boolean notifyByEmail(Long producerUserId, AgroclimaticAlertIssuedEvent event) {
        var email = externalIamService.fetchEmailByUserId(producerUserId);
        if (email.isEmpty()) {
            return false;
        }
        try {
            emailSender.send(email.get(), emailSubject(event), emailHtml(event));
            return true;
        } catch (EmailSender.DeliveryFailed exception) {
            // The email address is not logged: it is personal data
            LOGGER.warn("The alert email for producer {} was not sent: {}", producerUserId, exception.getMessage());
            return false;
        }
    }

    private boolean notifyByMessage(Long producerUserId, AgroclimaticAlertIssuedEvent event) {
        var phone = externalProfilesService.fetchPhoneNumberByUserId(producerUserId);
        if (phone.isEmpty()) {
            return false;
        }
        try {
            messageSender.send(phone.get(), messageBody(event));
            return true;
        } catch (MessageSender.DeliveryFailed exception) {
            // The phone is not logged: it is personal data
            LOGGER.warn("The alert message for producer {} was not sent: {}", producerUserId, exception.getMessage());
            return false;
        }
    }

    static String messageBody(AgroclimaticAlertIssuedEvent event) {
        return "SumaqAgro - %s (%s, severidad %s)%n%s".formatted(
                event.getTitle(), event.getRegion(), event.getSeverity(), event.getDescription());
    }

    static String emailSubject(AgroclimaticAlertIssuedEvent event) {
        return "SumaqAgro: %s en %s".formatted(event.getTitle(), event.getRegion());
    }

    static String emailHtml(AgroclimaticAlertIssuedEvent event) {
        return """
                <h2>%s</h2>
                <p><strong>Región:</strong> %s<br><strong>Severidad:</strong> %s</p>
                <p>%s</p>
                <p>Revisa tus parcelas en SumaqAgro y consulta a tu agrónomo si necesitas apoyo.</p>
                <p style="color:#666;font-size:12px">Fuente: %s</p>
                """.formatted(
                HtmlUtils.htmlEscape(event.getTitle()),
                HtmlUtils.htmlEscape(event.getRegion()),
                HtmlUtils.htmlEscape(event.getSeverity().name()),
                HtmlUtils.htmlEscape(event.getDescription()),
                HtmlUtils.htmlEscape(event.getAlertSource()));
    }
}
