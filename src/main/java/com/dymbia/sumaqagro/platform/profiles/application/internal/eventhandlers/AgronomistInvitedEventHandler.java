package com.dymbia.sumaqagro.platform.profiles.application.internal.eventhandlers;

import com.dymbia.sumaqagro.platform.profiles.application.queryservices.CooperativeQueryService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;
import com.dymbia.sumaqagro.platform.profiles.domain.model.events.AgronomistInvitedEvent;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetCooperativeByIdQuery;
import com.dymbia.sumaqagro.platform.shared.application.outboundservices.email.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Sends the invitation email when a cooperative invites an agronomist (US-19).
 * <p>
 * It runs after the invitation is committed: no email leaves for an invitation that was rolled back,
 * and a failing email provider never undoes the invitation.
 * </p>
 *
 * @since 1.1
 */
@Service
public class AgronomistInvitedEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgronomistInvitedEventHandler.class);
    static final String SUBJECT = "Te invitaron a SumaqAgro como agrónomo";
    static final String TEAM_SUBJECT = "Te agregaron al equipo técnico de una cooperativa en SumaqAgro";

    private final EmailSender emailSender;
    private final CooperativeQueryService cooperativeQueryService;
    private final URI invitationFrontendUrl;

    public AgronomistInvitedEventHandler(EmailSender emailSender,
                                         CooperativeQueryService cooperativeQueryService,
                                         @Value("${profiles.agronomist-invitation.frontend-url}") URI invitationFrontendUrl) {
        this.emailSender = emailSender;
        this.cooperativeQueryService = cooperativeQueryService;
        this.invitationFrontendUrl = invitationFrontendUrl;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(AgronomistInvitedEvent event) {
        var cooperativeName = cooperativeQueryService.handle(new GetCooperativeByIdQuery(event.cooperativeId()))
                .map(Cooperative::getBusinessName)
                .orElse("una cooperativa");
        try {
            if (event.accountExists()) {
                // The agronomist already signs in: the invitation was accepted, he only needs to sign in
                var signIn = UriComponentsBuilder.fromUri(invitationFrontendUrl)
                        .replacePath("/iam/sign-in")
                        .replaceQuery(null)
                        .build()
                        .toUri();
                emailSender.send(event.email(), TEAM_SUBJECT, teamHtmlContent(cooperativeName, signIn));
                return;
            }
            var link = UriComponentsBuilder.fromUri(invitationFrontendUrl)
                    .queryParam("invitationToken", event.invitationToken())
                    .queryParam("email", event.email())
                    .build()
                    .encode()
                    .toUri();
            emailSender.send(event.email(), SUBJECT, htmlContent(cooperativeName, link));
        } catch (EmailSender.DeliveryFailed exception) {
            // Neither the email nor the token are logged: the token gives access to the invitation
            LOGGER.warn("The invitation email of cooperative {} was not sent: {}",
                    event.cooperativeId(), exception.getMessage());
        }
    }

    static String teamHtmlContent(String cooperativeName, URI signIn) {
        return """
                <p>Hola,</p>
                <p><strong>%s</strong> te agregó a su equipo técnico en SumaqAgro.</p>
                <p>Ya puedes ver y asesorar las parcelas que te asigne: <a href="%s">ingresar a SumaqAgro</a>.</p>
                """.formatted(HtmlUtils.htmlEscape(cooperativeName), HtmlUtils.htmlEscape(signIn.toASCIIString()));
    }

    static String htmlContent(String cooperativeName, URI link) {
        return """
                <p>Hola,</p>
                <p><strong>%s</strong> te invitó a SumaqAgro para asesorar las parcelas de sus socios.</p>
                <p><a href="%s">Aceptar la invitación y crear mi cuenta</a></p>
                <p>Si no esperabas esta invitación, puedes ignorar este correo.</p>
                """.formatted(HtmlUtils.htmlEscape(cooperativeName), HtmlUtils.htmlEscape(link.toASCIIString()));
    }
}
