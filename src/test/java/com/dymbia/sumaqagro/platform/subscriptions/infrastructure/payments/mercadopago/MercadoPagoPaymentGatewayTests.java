package com.dymbia.sumaqagro.platform.subscriptions.infrastructure.payments.mercadopago;

import com.dymbia.sumaqagro.platform.subscriptions.application.internal.outboundservices.payments.PaymentGateway;
import com.dymbia.sumaqagro.platform.subscriptions.application.internal.outboundservices.payments.PaymentGateway.ProviderPaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MercadoPagoPaymentGatewayTests {

    @Test
    void onlyAnApprovedPaymentIsApproved() {
        assertEquals(ProviderPaymentStatus.APPROVED, MercadoPagoPaymentGateway.toStatus("approved"));
    }

    @Test
    void finalFailuresAreRejected() {
        for (var status : new String[]{"rejected", "cancelled", "refunded", "charged_back"}) {
            assertEquals(ProviderPaymentStatus.REJECTED, MercadoPagoPaymentGateway.toStatus(status));
        }
    }

    @Test
    void anythingNotFinalStaysPending() {
        for (var status : new String[]{"pending", "in_process", "authorized", "in_mediation", "unknown"}) {
            assertEquals(ProviderPaymentStatus.PENDING, MercadoPagoPaymentGateway.toStatus(status));
        }
        assertEquals(ProviderPaymentStatus.PENDING, MercadoPagoPaymentGateway.toStatus(null));
    }

    @Test
    void withoutAccessTokenTheGatewayIsUnavailableAndCallsNothing() {
        var gateway = new MercadoPagoPaymentGateway("https://api.mercadopago.com", "",
                "http://localhost:4200/subscriptions/payment-result", "");

        assertThrows(PaymentGateway.Unavailable.class, () -> gateway.createCheckout(
                new PaymentGateway.CheckoutRequest("mp-1", "Plan", BigDecimal.TEN, "PEN")));
        assertThrows(PaymentGateway.Unavailable.class, () -> gateway.findPayment("123"));
    }
}
