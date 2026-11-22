package com.dymbia.sumaqagro.platform.shared.infrastructure.messaging.twilio;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TwilioMessageSenderTests {

    @Test
    void aPeruvianMobileGetsTheCountryCode() {
        assertEquals(Optional.of("+51987654321"), TwilioMessageSender.toE164("987 654 321"));
        assertEquals(Optional.of("+51987654321"), TwilioMessageSender.toE164("987-654-321"));
    }

    @Test
    void aPhoneWithCountryCodeIsKept() {
        assertEquals(Optional.of("+51987654321"), TwilioMessageSender.toE164("+51 987 654 321"));
        assertEquals(Optional.of("+51987654321"), TwilioMessageSender.toE164("51987654321"));
        assertEquals(Optional.of("+14155238886"), TwilioMessageSender.toE164("+1 (415) 523-8886"));
    }

    @Test
    void somethingThatIsNotAPhoneIsRejected() {
        assertEquals(Optional.empty(), TwilioMessageSender.toE164("123"));
        assertEquals(Optional.empty(), TwilioMessageSender.toE164("mi celular"));
        assertEquals(Optional.empty(), TwilioMessageSender.toE164(null));
    }
}
