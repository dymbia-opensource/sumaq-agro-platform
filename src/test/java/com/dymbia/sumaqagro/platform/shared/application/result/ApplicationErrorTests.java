package com.dymbia.sumaqagro.platform.shared.application.result;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ApplicationErrorTests {

    @Test
    void shouldCreateErrorWithCodeAndMessageOnly() {
        var error = new ApplicationError("CODE", "message");

        assertEquals("CODE", error.code());
        assertEquals("message", error.message());
        assertNull(error.details());
    }

    @Test
    void shouldCreateValidationError() {
        var error = ApplicationError.validationError("name", "must not be blank");

        assertEquals("VALIDATION_ERROR", error.code());
        assertEquals("Validation failed: name", error.message());
        assertEquals("must not be blank", error.details());
    }

    @Test
    void shouldBuildTheNotFoundCodeFromTheResource() {
        var error = ApplicationError.notFound("FieldPlot", "7");

        assertEquals("FIELDPLOT_NOT_FOUND", error.code());
        assertEquals("FieldPlot not found: 7", error.message());
        assertNull(error.details());
    }

    @Test
    void shouldBuildTheConflictCodeFromTheResource() {
        var error = ApplicationError.conflict("FieldPlot", "name already exists");

        assertEquals("FIELDPLOT_CONFLICT", error.code());
        assertEquals("Conflict with FieldPlot", error.message());
        assertEquals("name already exists", error.details());
    }

    @Test
    void shouldCreateBusinessRuleViolation() {
        var error = ApplicationError.businessRuleViolation("plot-quota", "quota reached");

        assertEquals("BUSINESS_RULE_VIOLATION", error.code());
        assertEquals("Business rule violation: plot-quota", error.message());
        assertEquals("quota reached", error.details());
    }

    @Test
    void shouldCreateUnexpectedError() {
        var error = ApplicationError.unexpected("create-plot", "boom");

        assertEquals("UNEXPECTED_ERROR", error.code());
        assertEquals("Unexpected error in create-plot", error.message());
        assertEquals("boom", error.details());
    }
}
