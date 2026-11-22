package com.dymbia.sumaqagro.platform.profiles.application.internal.eventhandlers;

import com.dymbia.sumaqagro.platform.profiles.application.queryservices.CooperativeQueryService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;
import com.dymbia.sumaqagro.platform.profiles.domain.model.events.AgronomistInvitedEvent;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetCooperativeByIdQuery;
import com.dymbia.sumaqagro.platform.shared.application.outboundservices.email.EmailSender;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AgronomistInvitedEventHandlerTests {

    private final EmailSender emailSender = mock(EmailSender.class);
    private final CooperativeQueryService cooperativeQueryService = mock(CooperativeQueryService.class);
    private final AgronomistInvitedEventHandler handler = new AgronomistInvitedEventHandler(emailSender,
            cooperativeQueryService, URI.create("http://localhost:4200/iam/sign-up"));

    private static final AgronomistInvitedEvent EVENT =
            new AgronomistInvitedEvent(3L, "agronomo@example.com", "token-123");

    private String sentHtml() {
        var html = ArgumentCaptor.forClass(String.class);
        verify(emailSender).send(eq("agronomo@example.com"), eq(AgronomistInvitedEventHandler.SUBJECT), html.capture());
        return html.getValue();
    }

    @Test
    void theInvitationEmailNamesTheCooperativeAndLinksToSignUpWithTheToken() {
        var cooperative = mock(Cooperative.class);
        when(cooperative.getBusinessName()).thenReturn("Cooperativa Valle del Mantaro");
        when(cooperativeQueryService.handle(any(GetCooperativeByIdQuery.class))).thenReturn(Optional.of(cooperative));

        handler.on(EVENT);

        var html = sentHtml();
        assertTrue(html.contains("Cooperativa Valle del Mantaro"));
        assertTrue(html.contains("http://localhost:4200/iam/sign-up?invitationToken=token-123"));
    }

    @Test
    void theCooperativeNameIsEscapedSoItCannotInjectHtml() {
        var cooperative = mock(Cooperative.class);
        when(cooperative.getBusinessName()).thenReturn("<script>alert(1)</script>");
        when(cooperativeQueryService.handle(any(GetCooperativeByIdQuery.class))).thenReturn(Optional.of(cooperative));

        handler.on(EVENT);

        var html = sentHtml();
        assertFalse(html.contains("<script>"));
        assertTrue(html.contains("&lt;script&gt;"));
    }

    @Test
    void aFailingProviderDoesNotBreakTheInvitation() {
        when(cooperativeQueryService.handle(any(GetCooperativeByIdQuery.class))).thenReturn(Optional.empty());
        doThrow(new EmailSender.DeliveryFailed("Brevo rejected the email with status 401"))
                .when(emailSender).send(anyString(), anyString(), anyString());

        assertDoesNotThrow(() -> handler.on(EVENT));
        assertEquals(1, org.mockito.Mockito.mockingDetails(emailSender).getInvocations().size());
    }
}
