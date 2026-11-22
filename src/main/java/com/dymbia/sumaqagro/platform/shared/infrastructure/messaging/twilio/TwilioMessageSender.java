package com.dymbia.sumaqagro.platform.shared.infrastructure.messaging.twilio;

import com.dymbia.sumaqagro.platform.shared.application.outboundservices.messaging.MessageSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Optional;

/**
 * Implementation of {@link MessageSender} with the Twilio Messaging API
 * ({@code POST /2010-04-01/Accounts/{AccountSid}/Messages.json}).
 * <p>
 * It works for SMS and for WhatsApp: when the sender number starts with {@code whatsapp:} (the Twilio WhatsApp
 * sandbox), the destination gets the same prefix. Active only when {@code TWILIO_ACCOUNT_SID} is set.
 * With a trial account Twilio only delivers to verified numbers, and the WhatsApp sandbox only to phones that
 * sent the {@code join} code.
 * </p>
 *
 * @since 1.1
 */
@Component
@ConditionalOnExpression("!'${notifications.twilio.account-sid:}'.isBlank()")
public class TwilioMessageSender implements MessageSender {

    private static final String WHATSAPP_PREFIX = "whatsapp:";
    private static final String PERU_PREFIX = "+51";

    private final RestClient client;
    private final String accountSid;
    private final String fromNumber;

    public TwilioMessageSender(@Value("${notifications.twilio.base-url}") String baseUrl,
                               @Value("${notifications.twilio.account-sid}") String accountSid,
                               @Value("${notifications.twilio.auth-token}") String authToken,
                               @Value("${notifications.twilio.from-number}") String fromNumber) {
        if (authToken.isBlank() || fromNumber.isBlank()) {
            throw new IllegalStateException("Twilio messages require TWILIO_AUTH_TOKEN and TWILIO_FROM_NUMBER");
        }
        this.accountSid = accountSid;
        this.fromNumber = fromNumber.strip();
        var httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
        var requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(15));
        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultHeaders(headers -> headers.setBasicAuth(accountSid, authToken))
                .build();
    }

    @Override
    public void send(String phoneNumber, String body) {
        var destination = toE164(phoneNumber)
                .orElseThrow(() -> new DeliveryFailed("The phone number is not valid"));
        var form = new LinkedMultiValueMap<String, String>();
        form.add("From", fromNumber);
        form.add("To", fromNumber.startsWith(WHATSAPP_PREFIX) ? WHATSAPP_PREFIX + destination : destination);
        form.add("Body", body);
        try {
            client.post()
                    .uri("/2010-04-01/Accounts/{accountSid}/Messages.json", accountSid)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException exception) {
            // Only the status: the error body can echo the phone number
            throw new DeliveryFailed("Twilio rejected the message with status " + exception.getStatusCode().value());
        } catch (RestClientException exception) {
            throw new DeliveryFailed("Twilio could not be reached");
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Turns the phone of a profile into the E.164 format that Twilio needs. A 9-digit Peruvian mobile
     * ({@code 987 654 321}) gets the {@code +51} country code.
     *
     * @param phoneNumber the phone as written in the profile
     * @return the phone in E.164, or empty when it is not a phone number
     */
    static Optional<String> toE164(String phoneNumber) {
        if (phoneNumber == null) {
            return Optional.empty();
        }
        var cleaned = phoneNumber.replaceAll("[\\s()\\-.]", "");
        if (cleaned.matches("9\\d{8}")) {
            return Optional.of(PERU_PREFIX + cleaned);
        }
        if (cleaned.matches("51\\d{9}")) {
            return Optional.of("+" + cleaned);
        }
        if (cleaned.matches("\\+[1-9]\\d{7,14}")) {
            return Optional.of(cleaned);
        }
        return Optional.empty();
    }
}
