package com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ProblemDetailFromApplicationErrorAssemblerTests {

    @AfterEach
    void resetLocale() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void shouldBuildProblemDetailWithStatusTypeTitleAndCode() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        var problem = ProblemDetailFromApplicationErrorAssembler
                .toProblemDetailFromApplicationError(ApplicationError.notFound("FieldPlot", "7"));

        assertEquals(HttpStatus.NOT_FOUND.value(), problem.getStatus());
        assertEquals(URI.create("/api/v1/errors/not-found"), problem.getType());
        assertEquals("Resource Not Found", problem.getTitle());
        assertEquals("FIELDPLOT_NOT_FOUND", problem.getProperties().get("code"));
        assertFalse(problem.getProperties().containsKey("details"));
    }

    @Test
    void shouldTranslateTheMessageToTheLanguageOfTheRequest() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        var english = ProblemDetailFromApplicationErrorAssembler
                .toProblemDetailFromApplicationError(ApplicationError.notFound("FieldPlot", "7"));

        LocaleContextHolder.setLocale(Locale.forLanguageTag("es"));
        var spanish = ProblemDetailFromApplicationErrorAssembler
                .toProblemDetailFromApplicationError(ApplicationError.notFound("FieldPlot", "7"));

        assertEquals("Field plot not found", english.getDetail());
        assertEquals("Parcela no encontrada", spanish.getDetail());
    }

    @Test
    void shouldUseTheGenericMessageOfTheCategoryWhenThereIsNoSpecificOne() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        var problem = ProblemDetailFromApplicationErrorAssembler.toProblemDetailFromApplicationError(
                ApplicationError.conflict("Course", "Title 'Java' already exists"));

        assertEquals("Resource conflict", problem.getDetail());
        assertEquals("Title 'Java' already exists", problem.getProperties().get("details"));
    }

    @Test
    void shouldMapBusinessRuleViolationToUnprocessableEntity() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        var problem = ProblemDetailFromApplicationErrorAssembler.toProblemDetailFromApplicationError(
                ApplicationError.businessRuleViolation("plot-quota", "quota reached"));

        assertEquals(422, problem.getStatus());
        assertEquals("Business rule violation", problem.getDetail());
    }

    @Test
    void shouldFallBackToTheGenericMessageWhenTheCodeIsUnknown() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        var problem = ProblemDetailFromApplicationErrorAssembler
                .toProblemDetailFromApplicationError(new ApplicationError("SOMETHING_ELSE", "Custom message"));

        assertEquals("Application error", problem.getDetail());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), problem.getStatus());
    }

    @Test
    void shouldMapEveryErrorCodeToItsHttpStatus() {
        assertEquals(HttpStatus.BAD_REQUEST, ProblemDetailFromApplicationErrorAssembler.toStatusFromErrorCode("VALIDATION_ERROR"));
        assertEquals(HttpStatus.NOT_FOUND, ProblemDetailFromApplicationErrorAssembler.toStatusFromErrorCode("FIELDPLOT_NOT_FOUND"));
        assertEquals(HttpStatus.CONFLICT, ProblemDetailFromApplicationErrorAssembler.toStatusFromErrorCode("FIELDPLOT_CONFLICT"));
        assertEquals(422, ProblemDetailFromApplicationErrorAssembler.toStatusFromErrorCode("BUSINESS_RULE_VIOLATION").value());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ProblemDetailFromApplicationErrorAssembler.toStatusFromErrorCode("UNEXPECTED_ERROR"));
    }

    @Test
    void shouldBuildTheResponseWithTheStatusOfTheError() {
        var response = ProblemDetailFromApplicationErrorAssembler
                .toResponseFromApplicationError(ApplicationError.conflict("FieldPlot", "name already exists"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
    }
}
