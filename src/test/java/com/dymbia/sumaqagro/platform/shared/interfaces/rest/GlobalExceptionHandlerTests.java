package com.dymbia.sumaqagro.platform.shared.interfaces.rest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTests {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @AfterEach
    void resetLocale() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void illegalArgumentBecomesBadRequestWithItsMessageInDetails() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        var response = handler.handleIllegalArgumentException(new IllegalArgumentException("Plot name must not be blank."));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed", response.getBody().getDetail());
        assertEquals("Plot name must not be blank.", response.getBody().getProperties().get("details"));
    }

    @Test
    void illegalArgumentWithoutMessageUsesTheGenericValidationMessage() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        var response = handler.handleIllegalArgumentException(new IllegalArgumentException());

        assertEquals("Request validation failed", response.getBody().getProperties().get("details"));
    }

    @Test
    void runtimeExceptionBecomesInternalServerError() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        var response = handler.handleRuntimeException(new IllegalStateException("something failed"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error", response.getBody().getDetail());
        assertEquals("something failed", response.getBody().getProperties().get("details"));
    }

    @Test
    void anyOtherExceptionBecomesInternalServerError() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        var response = handler.handleException(new Exception());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred", response.getBody().getProperties().get("details"));
    }
}
