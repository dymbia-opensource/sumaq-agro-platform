package com.dymbia.sumaqagro.platform.subscriptions.infrastructure.payments.mercadopago;

import com.dymbia.sumaqagro.platform.subscriptions.application.internal.outboundservices.payments.PaymentGateway;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of {@link PaymentGateway} with Mercado Pago Checkout Pro (API of preferences).
 * <ul>
 *     <li>{@code POST /checkout/preferences} creates the checkout and answers its links.</li>
 *     <li>{@code GET /v1/payments/{id}} reads the payment, with our reference in {@code external_reference}.</li>
 * </ul>
 * The access token goes in the {@code Authorization: Bearer} header and is never logged.
 *
 * @since 1.1
 */
@Component
public class MercadoPagoPaymentGateway implements PaymentGateway {

    private final RestClient client;
    private final String accessToken;
    private final String returnUrl;
    private final String notificationUrl;

    public MercadoPagoPaymentGateway(@Value("${payments.mercadopago.base-url}") String baseUrl,
                                     @Value("${payments.mercadopago.access-token}") String accessToken,
                                     @Value("${payments.mercadopago.return-url}") String returnUrl,
                                     @Value("${payments.mercadopago.notification-url}") String notificationUrl) {
        this.accessToken = accessToken;
        this.returnUrl = returnUrl;
        this.notificationUrl = notificationUrl;
        var httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
        var requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(15));
        this.client = RestClient.builder().baseUrl(baseUrl).requestFactory(requestFactory).build();
    }

    @Override
    public CheckoutSession createCheckout(CheckoutRequest request) {
        ensureConfigured();
        var body = new HashMap<String, Object>();
        body.put("items", List.of(Map.of(
                "id", request.reference(),
                "title", request.title(),
                "quantity", 1,
                "unit_price", request.amount(),
                "currency_id", request.currency())));
        body.put("external_reference", request.reference());
        body.put("back_urls", Map.of("success", returnUrl, "failure", returnUrl, "pending", returnUrl));
        // Mercado Pago only returns by itself to a public https page; on localhost the buyer clicks "Volver"
        if (returnUrl.startsWith("https://")) {
            body.put("auto_return", "approved");
        }
        if (!notificationUrl.isBlank()) {
            body.put("notification_url", notificationUrl);
        }
        try {
            var preference = client.post()
                    .uri("/checkout/preferences")
                    .header("Authorization", "Bearer " + accessToken)
                    .header("X-Idempotency-Key", request.reference())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Preference.class);
            if (preference == null || preference.id() == null || preference.initPoint() == null) {
                throw new Unavailable("Mercado Pago answered an empty preference");
            }
            return new CheckoutSession(preference.id(), URI.create(preference.initPoint()),
                    preference.sandboxInitPoint() == null ? null : URI.create(preference.sandboxInitPoint()));
        } catch (RestClientResponseException exception) {
            // Only the status: the body of an error can echo the request
            throw new Unavailable("Mercado Pago rejected the preference with status " + exception.getStatusCode().value());
        } catch (RestClientException | IllegalArgumentException exception) {
            throw new Unavailable("Mercado Pago could not be reached");
        }
    }

    @Override
    public Optional<ProviderPayment> findPayment(String providerPaymentId) {
        ensureConfigured();
        try {
            var payment = client.get()
                    .uri("/v1/payments/{id}", providerPaymentId)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .body(MercadoPagoPayment.class);
            if (payment == null || payment.id() == null) {
                return Optional.empty();
            }
            return Optional.of(new ProviderPayment(payment.id().toString(), payment.externalReference(),
                    toStatus(payment.status()), payment.transactionAmount(), payment.currencyId()));
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
                return Optional.empty();
            }
            throw new Unavailable("Mercado Pago rejected the payment lookup with status "
                    + exception.getStatusCode().value());
        } catch (RestClientException exception) {
            throw new Unavailable("Mercado Pago could not be reached");
        }
    }

    /**
     * Maps the Mercado Pago payment status. Anything that is not final yet stays pending.
     *
     * @param status the Mercado Pago status
     * @return the provider payment status
     */
    static ProviderPaymentStatus toStatus(String status) {
        if (status == null) {
            return ProviderPaymentStatus.PENDING;
        }
        return switch (status) {
            case "approved" -> ProviderPaymentStatus.APPROVED;
            case "rejected", "cancelled", "refunded", "charged_back" -> ProviderPaymentStatus.REJECTED;
            default -> ProviderPaymentStatus.PENDING; // pending, in_process, authorized, in_mediation
        };
    }

    private void ensureConfigured() {
        if (accessToken == null || accessToken.isBlank()) {
            throw new Unavailable("Mercado Pago is not configured: set MERCADOPAGO_ACCESS_TOKEN");
        }
    }

    private record Preference(String id,
                              @JsonProperty("init_point") String initPoint,
                              @JsonProperty("sandbox_init_point") String sandboxInitPoint) {
    }

    private record MercadoPagoPayment(Long id,
                                      String status,
                                      @JsonProperty("external_reference") String externalReference,
                                      @JsonProperty("transaction_amount") BigDecimal transactionAmount,
                                      @JsonProperty("currency_id") String currencyId) {
    }
}
