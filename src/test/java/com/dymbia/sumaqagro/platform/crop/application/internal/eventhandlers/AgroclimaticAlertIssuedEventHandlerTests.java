package com.dymbia.sumaqagro.platform.crop.application.internal.eventhandlers;

import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.acl.ExternalFarmingService;
import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.acl.ExternalProfilesService;
import com.dymbia.sumaqagro.platform.crop.domain.model.events.AgroclimaticAlertIssuedEvent;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.AgroclimaticAlertSeverity;
import com.dymbia.sumaqagro.platform.shared.application.outboundservices.messaging.MessageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AgroclimaticAlertIssuedEventHandlerTests {

    private final ExternalFarmingService externalFarmingService = mock(ExternalFarmingService.class);
    private final ExternalProfilesService externalProfilesService = mock(ExternalProfilesService.class);
    private final MessageSender messageSender = mock(MessageSender.class);
    private final AgroclimaticAlertIssuedEventHandler handler = new AgroclimaticAlertIssuedEventHandler(
            externalFarmingService, externalProfilesService, messageSender);

    private static AgroclimaticAlertIssuedEvent alert(AgroclimaticAlertSeverity severity) {
        return new AgroclimaticAlertIssuedEvent(new Object(), "Riesgo de helada",
                "Se pronostica una temperatura mínima de -1.2 °C el 24/11.", severity, "Junín",
                Instant.parse("2026-11-23T11:00:00Z"), "AgroMonitoring");
    }

    @BeforeEach
    void twoProducersInTheRegionOneWithoutPhone() {
        when(messageSender.isEnabled()).thenReturn(true);
        when(externalFarmingService.fetchProducerUserIdsByRegion("Junín")).thenReturn(List.of(1L, 2L));
        when(externalProfilesService.fetchPhoneNumberByUserId(1L)).thenReturn(Optional.of("987 654 321"));
        when(externalProfilesService.fetchPhoneNumberByUserId(2L)).thenReturn(Optional.empty());
    }

    @Test
    void aSeriousAlertReachesTheProducersOfTheRegionThatHaveAPhone() {
        handler.on(alert(AgroclimaticAlertSeverity.HIGH));

        verify(messageSender).send(eq("987 654 321"), anyString());
        verify(messageSender, org.mockito.Mockito.times(1)).send(anyString(), anyString());
    }

    @Test
    void aMinorAlertSendsNoMessage() {
        handler.on(alert(AgroclimaticAlertSeverity.MEDIUM));

        verify(messageSender, never()).send(anyString(), anyString());
    }

    @Test
    void withoutTwilioNothingIsLookedUp() {
        when(messageSender.isEnabled()).thenReturn(false);

        handler.on(alert(AgroclimaticAlertSeverity.CRITICAL));

        verify(externalFarmingService, never()).fetchProducerUserIdsByRegion(anyString());
    }

    @Test
    void aFailingMessageDoesNotStopTheOthers() {
        when(externalProfilesService.fetchPhoneNumberByUserId(2L)).thenReturn(Optional.of("912 345 678"));
        doThrow(new MessageSender.DeliveryFailed("Twilio rejected the message with status 400"))
                .when(messageSender).send(eq("987 654 321"), anyString());

        assertDoesNotThrow(() -> handler.on(alert(AgroclimaticAlertSeverity.HIGH)));

        verify(messageSender).send(eq("912 345 678"), anyString());
    }

    @Test
    void theMessageSaysWhatHappensAndWhere() {
        var body = AgroclimaticAlertIssuedEventHandler.messageBody(alert(AgroclimaticAlertSeverity.HIGH));

        assertTrue(body.contains("Riesgo de helada"));
        assertTrue(body.contains("Junín"));
        assertTrue(body.contains("-1.2 °C"));
    }
}
