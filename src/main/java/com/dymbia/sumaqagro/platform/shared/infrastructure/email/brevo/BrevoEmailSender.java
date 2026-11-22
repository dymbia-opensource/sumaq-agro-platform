package com.dymbia.sumaqagro.platform.shared.infrastructure.email.brevo;

import com.dymbia.sumaqagro.platform.shared.application.outboundservices.email.EmailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link EmailSender} with the Brevo transactional email API
 * ({@code POST https://api.brevo.com/v3/smtp/email}).
 * <p>
 * Active only when {@code notifications.email.enabled=true}. The API key needs the
 * "Transactional emails" permission and the sender must be verified in Brevo.
 * </p>
 *
 * @since 1.1
 */
@Component
@ConditionalOnProperty(name = "notifications.email.enabled", havingValue = "true")
public class BrevoEmailSender implements EmailSender {

    private final RestClient client;
    private final String senderEmail;
    private final String senderName;

    public BrevoEmailSender(@Value("${notifications.email.brevo.api-key}") String apiKey,
                            @Value("${notifications.email.brevo.sender-email}") String senderEmail,
                            @Value("${notifications.email.brevo.sender-name}") String senderName) {
        if (apiKey.isBlank() || senderEmail.isBlank()) {
            throw new IllegalStateException("Email notifications require BREVO_API_KEY and BREVO_SENDER_EMAIL");
        }
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3_000);
        requestFactory.setReadTimeout(5_000);
        this.senderEmail = senderEmail;
        this.senderName = senderName;
        this.client = RestClient.builder()
                .baseUrl("https://api.brevo.com")
                .requestFactory(requestFactory)
                .defaultHeader("api-key", apiKey)
                .build();
    }

    @Override
    public void send(String recipientEmail, String subject, String htmlContent) {
        var message = Map.of(
                "sender", Map.of("email", senderEmail, "name", senderName),
                "to", List.of(Map.of("email", recipientEmail)),
                "subject", subject,
                "htmlContent", htmlContent);
        try {
            client.post()
                    .uri("/v3/smtp/email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(message)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException exception) {
            // Only the status code: the body could echo the recipient or the content
            throw new EmailSender.DeliveryFailed("Brevo rejected the email with status " + exception.getStatusCode().value());
        } catch (RestClientException exception) {
            throw new EmailSender.DeliveryFailed("Brevo could not be reached");
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
