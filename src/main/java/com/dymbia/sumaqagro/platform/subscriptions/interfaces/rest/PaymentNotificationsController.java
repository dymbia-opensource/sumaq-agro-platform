package com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest;

import com.dymbia.sumaqagro.platform.subscriptions.application.commandservices.SubscriptionCommandService;
import com.dymbia.sumaqagro.platform.subscriptions.domain.commands.ConfirmPaymentCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Receives the Mercado Pago payment notifications (webhook). It is public because Mercado Pago calls it
 * without a token, so it trusts nothing in the request: it only takes the payment id and reads the
 * payment from Mercado Pago with our access token before applying it.
 * <p>
 * It only works when the back has a public URL configured in {@code MERCADOPAGO_NOTIFICATION_URL};
 * on localhost the frontend confirms with {@code POST /api/v1/subscriptions/me/payment-confirmations}.
 * </p>
 *
 * @since 1.1
 */
@RestController
@RequestMapping("/api/v1/payment-notifications")
@Tag(name = "Subscriptions", description = "Subscription and payment endpoints")
public class PaymentNotificationsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentNotificationsController.class);

    private final SubscriptionCommandService subscriptionCommandService;

    public PaymentNotificationsController(SubscriptionCommandService subscriptionCommandService) {
        this.subscriptionCommandService = subscriptionCommandService;
    }

    @Operation(summary = "Mercado Pago payment notification (webhook)",
            description = "Called by Mercado Pago. Answers 200 so Mercado Pago does not retry; the payment is read "
                    + "from Mercado Pago before it changes any subscription.")
    @PostMapping
    public ResponseEntity<Void> receive(@RequestParam(value = "type", required = false) String type,
                                        @RequestParam(value = "topic", required = false) String topic,
                                        @RequestParam(value = "data.id", required = false) String dataId,
                                        @RequestParam(value = "id", required = false) String id,
                                        @RequestBody(required = false) Map<String, Object> body) {
        var paymentId = paymentIdOf(type, topic, dataId, id, body);
        if (paymentId == null) {
            return ResponseEntity.ok().build();
        }
        try {
            var result = subscriptionCommandService.handle(new ConfirmPaymentCommand(paymentId, null));
            if (result.isFailure()) {
                LOGGER.warn("Mercado Pago notification for payment {} was not applied: {}", paymentId, result);
            }
        } catch (IllegalArgumentException exception) {
            LOGGER.warn("Mercado Pago notification ignored: invalid payment id");
        }
        return ResponseEntity.ok().build();
    }

    // Mercado Pago sends the payment id as "data.id" (webhooks) or "id" with topic=payment (IPN), or in the body
    private static String paymentIdOf(String type, String topic, String dataId, String id, Map<String, Object> body) {
        var kind = type != null ? type : topic;
        if (body != null && kind == null && body.get("type") instanceof String bodyType) {
            kind = bodyType;
        }
        if (!"payment".equals(kind)) {
            return null;
        }
        if (dataId != null) {
            return dataId;
        }
        if (body != null && body.get("data") instanceof Map<?, ?> data && data.get("id") != null) {
            return data.get("id").toString();
        }
        return id;
    }
}
