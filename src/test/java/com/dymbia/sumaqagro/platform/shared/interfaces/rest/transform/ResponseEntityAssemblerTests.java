package com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ResponseEntityAssemblerTests {

    @Test
    void successResultBecomesTheResourceWithTheGivenStatus() {
        var response = ResponseEntityAssembler.toResponseEntityFromResult(
                Result.<Integer, ApplicationError>success(7), value -> "resource-" + value, HttpStatus.CREATED);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("resource-7", response.getBody());
    }

    @Test
    void failureResultBecomesTheProblemDetailOfItsError() {
        var response = ResponseEntityAssembler.toResponseEntityFromResult(
                Result.<Integer, ApplicationError>failure(ApplicationError.conflict("FieldPlot", "errors.found")),
                value -> "resource-" + value, HttpStatus.CREATED);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertInstanceOf(ProblemDetail.class, response.getBody());
    }
}
