package com.dymbia.sumaqagro.platform.crop.application.internal.eventhandlers;

import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.acl.ExternalFarmingService;
import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.acl.ExternalIamService;
import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.acl.ExternalProfilesService;
import com.dymbia.sumaqagro.platform.crop.domain.model.events.AgroclimaticAlertIssuedEvent;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.AgroclimaticAlertSeverity;
import com.dymbia.sumaqagro.platform.shared.application.outboundservices.email.EmailSender;
import com.dymbia.sumaqagro.platform.shared.application.outboundservices.messaging.MessageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AgroclimaticAlertIssuedEventHandlerTests {

    private final ExternalFarmingService externalFarmingService = mock(ExternalFarmingService.class);
    private final ExternalProfilesService externalProfilesService = mock(ExternalProfilesService.class);
    private final ExternalIamService externalIamService = mock(ExternalIamService.class);
    private final MessageSender messageSender = mock(MessageSender.class);
    private final EmailSender emailSender = mock(EmailSender.class);
    private final AgroclimaticAlertIssuedEventHandler handler = new AgroclimaticAlertIssuedEventHandler(
            externalFarmingService, externalProfilesService, externalIamService, messageSender, emailSender);

    private static AgroclimaticAlertIssuedEvent alert(AgroclimaticAlertSeverity severity) {
        return new AgroclimaticAlertIssuedEvent(new Object(), "Riesgo de helada",
                "Se pronostica una temperatura mínima de -1.2 °C el 24/11.", severity, "Junín",
                Instant.parse("2026-11-23T11:00:00Z"), "AgroMonitoring");
    }

    @BeforeEach
    void twoProducersInTheRegion() {
        when(emailSender.isEnabled()).thenReturn(true);
        when(messageSender.isEnabled()).thenReturn(true);
        when(externalFarmingService.fetchProducerUserIdsByRegion("Junín")).thenReturn(List.of(1L, 2L));
        when(externalIamService.fetchEmailByUserId(1L)).thenReturn(Optional.of("productor1@example.com"));
        when(externalIamService.fetchEmailByUserId(2L)).thenReturn(Optional.of("productor2@example.com"));
        when(externalProfilesService.fetchPhoneNumberByUserId(1L)).thenReturn(Optional.of("987 654 321"));
        when(externalProfilesService.fetchPhoneNumberByUserId(2L)).thenReturn(Optional.empty());
    }

    @Test
    void aSeriousAlertIsEmailedToEveryProducerOfTheRegion() {
        handler.on(alert(AgroclimaticAlertSeverity.HIGH));

        verify(emailSender).send(eq("productor1@example.com"), anyString(), anyString());
        verify(emailSender).send(eq("productor2@example.com"), anyString(), anyString());
    }

    @Test
    void aSeriousAlertIsAlsoSentToTheProducersThatHaveAPhone() {
        handler.on(alert(AgroclimaticAlertSeverity.HIGH));

        verify(messageSender).send(eq("987 654 321"), anyString());
        verify(messageSender, times(1)).send(anyString(), anyString());
    }

    @Test
    void aMinorAlertNotifiesNobody() {
        handler.on(alert(AgroclimaticAlertSeverity.MEDIUM));

        verify(emailSender, never()).send(anyString(), anyString(), anyString());
        verify(messageSender, never()).send(anyString(), anyString());
    }

    @Test
    void withoutEmailNorMessagingNothingIsLookedUp() {
        when(emailSender.isEnabled()).thenReturn(false);
        when(messageSender.isEnabled()).thenReturn(false);

        handler.on(alert(AgroclimaticAlertSeverity.CRITICAL));

        verify(externalFarmingService, never()).fetchProducerUserIdsByRegion(anyString());
    }

    @Test
    void withEmailOnlyNoPhoneIsLookedUp() {
        when(messageSender.isEnabled()).thenReturn(false);

        handler.on(alert(AgroclimaticAlertSeverity.HIGH));

        verify(emailSender, times(2)).send(anyString(), anyString(), anyString());
        verify(externalProfilesService, never()).fetchPhoneNumberByUserId(anyLong());
    }

    @Test
    void aFailingEmailDoesNotStopTheOthers() {
        doThrow(new EmailSender.DeliveryFailed("Brevo rejected the email with status 400"))
                .when(emailSender).send(eq("productor1@example.com"), anyString(), anyString());

        assertDoesNotThrow(() -> handler.on(alert(AgroclimaticAlertSeverity.HIGH)));

        verify(emailSender).send(eq("productor2@example.com"), anyString(), anyString());
    }

    @Test
    void theEmailSaysWhatHappensAndWhereAndEscapesHtml() {
        var html = AgroclimaticAlertIssuedEventHandler.emailHtml(new AgroclimaticAlertIssuedEvent(new Object(),
                "Riesgo de helada", "<script>x</script>", AgroclimaticAlertSeverity.HIGH, "Junín",
                Instant.now(), "AgroMonitoring"));

        assertTrue(html.contains("Riesgo de helada"));
        assertTrue(html.contains("Jun"));
        assertFalse(html.contains("<script>"));
        assertTrue(AgroclimaticAlertIssuedEventHandler.emailSubject(alert(AgroclimaticAlertSeverity.HIGH))
                .contains("Riesgo de helada"));
    }
}
