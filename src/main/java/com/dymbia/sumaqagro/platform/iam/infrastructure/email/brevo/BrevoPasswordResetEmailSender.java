package com.dymbia.sumaqagro.platform.iam.infrastructure.email.brevo;

import com.dymbia.sumaqagro.platform.iam.application.internal.outboundservices.email.PasswordResetEmailSender;
import com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects.EmailAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.HtmlUtils;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Sends IAM password recovery emails through Brevo.
 */
@Component
@ConditionalOnProperty(name = "iam.password-reset.email.enabled", havingValue = "true")
public class BrevoPasswordResetEmailSender implements PasswordResetEmailSender {

    private final RestClient client;
    private final String senderEmail;

    public BrevoPasswordResetEmailSender(
            @Value("${iam.password-reset.email.brevo.api-key}") String apiKey,
            @Value("${iam.password-reset.email.brevo.sender-email}") String senderEmail
    ) {
        if (apiKey.isBlank() || senderEmail.isBlank()) {
            throw new IllegalStateException(
                    "Password recovery email requires BREVO_API_KEY and BREVO_SENDER_EMAIL");
        }

        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3_000);
        requestFactory.setReadTimeout(5_000);

        this.senderEmail = senderEmail;
        this.client = RestClient.builder()
                .baseUrl("https://api.brevo.com")
                .requestFactory(requestFactory)
                .defaultHeader("api-key", apiKey)
                .build();
    }

    @Override
    public void send(EmailAddress recipient, URI resetLink) {
        String htmlContent = """
                <p>Recibimos una solicitud para restablecer tu contraseña en SumaqAgro.</p>
                <p><a href="%s">Restablecer contraseña</a></p>
                <p>Si no solicitaste este cambio, puedes ignorar este correo.</p>
                """.formatted(HtmlUtils.htmlEscape(resetLink.toASCIIString()));

        var message = Map.of(
                "sender", Map.of("email", senderEmail, "name", "SumaqAgro"),
                "to", List.of(Map.of("email", recipient.value())),
                "subject", "Restablece tu contraseña de SumaqAgro",
                "htmlContent", htmlContent
        );

        try {
            client.post()
                    .uri("/v3/smtp/email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(message)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            // Keep provider details and recovery links out of application logs.
            throw new IllegalStateException("Password recovery email delivery failed");
        }
    }
}