package com.dymbia.sumaqagro.platform.crop.application.internal.eventhandlers;

import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.acl.ExternalFarmingService;
import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.acl.ExternalIamService;
import com.dymbia.sumaqagro.platform.crop.domain.model.events.AgroclimaticAlertIssuedEvent;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.AgroclimaticAlertSeverity;
import com.dymbia.sumaqagro.platform.shared.application.outboundservices.email.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.util.HtmlUtils;

import java.util.Set;

/**
 * Logs newly issued agroclimatic alerts and, when they are serious, emails them (Brevo) to the producers
 * with plots in the region, at the address of their account.
 * <p>
 * It runs after the alert is stored, and a failing email never undoes the alert: the producer still
 * sees it in the application.
 * </p>
 */
@Service
public class AgroclimaticAlertIssuedEventHandler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AgroclimaticAlertIssuedEventHandler.class);
    // Only serious alerts reach the producer, so they are not flooded with emails
    static final Set<AgroclimaticAlertSeverity> NOTIFIED_SEVERITIES =
            Set.of(AgroclimaticAlertSeverity.HIGH, AgroclimaticAlertSeverity.CRITICAL);

    private final ExternalFarmingService externalFarmingService;
    private final ExternalIamService externalIamService;
    private final EmailSender emailSender;

    public AgroclimaticAlertIssuedEventHandler(ExternalFarmingService externalFarmingService,
                                               ExternalIamService externalIamService,
                                               EmailSender emailSender) {
        this.externalFarmingService = externalFarmingService;
        this.externalIamService = externalIamService;
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
        if (!NOTIFIED_SEVERITIES.contains(event.getSeverity()) || !emailSender.isEnabled()) {
            return;
        }

        int sent = 0;
        for (var producerUserId : externalFarmingService.fetchProducerUserIdsByRegion(event.getRegion())) {
            var email = externalIamService.fetchEmailByUserId(producerUserId);
            if (email.isEmpty()) {
                continue;
            }
            try {
                emailSender.send(email.get(), emailSubject(event), emailHtml(event));
                sent++;
            } catch (EmailSender.DeliveryFailed exception) {
                // The email address is not logged: it is personal data
                LOGGER.warn("The alert email for producer {} was not sent: {}", producerUserId,
                        exception.getMessage());
            }
        }
        LOGGER.info("Agroclimatic alert '{}' of {} sent by {} email(s)", event.getTitle(), event.getRegion(), sent);
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
