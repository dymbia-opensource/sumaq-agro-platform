
package com.dymbia.sumaqagro.platform.shared.application.result;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ResultTests {

    @Test
    void shouldCreateSuccessfulResult() {
        Result<String, String> result =
                Result.success("Plot registered");

        assertTrue(result.isSuccess());
        assertFalse(result.isFailure());

        assertEquals("Plot registered", result.getOrElse("none"));
    }

    @Test
    void shouldCreateFailedResult() {
        Result<String, String> result =
                Result.failure("Plot already exists");

        assertTrue(result.isFailure());
        assertFalse(result.isSuccess());

        assertEquals("none", result.getOrElse("none"));
        assertEquals("Plot already exists",
                result.mapError(error -> error).recover(error -> Result.success(error)).getOrElse("none"));
    }

    @Test
    void shouldConvertToOptional() {
        assertEquals(Optional.of(5), Result.<Integer, String>success(5).toOptional());
        assertEquals(Optional.empty(), Result.<Integer, String>failure("error").toOptional());
    }

    @Test
    void shouldReturnDefaultValueOnlyForFailure() {
        assertEquals(5, Result.<Integer, String>success(5).getOrElse(0));
        assertEquals(0, Result.<Integer, String>failure("error").getOrElse(0));
    }

    @Test
    void shouldMapTheValueOfASuccessAndKeepAFailure() {
        assertEquals(Optional.of("5"), Result.<Integer, String>success(5).map(String::valueOf).toOptional());

        var failure = Result.<Integer, String>failure("error").map(String::valueOf);

        assertTrue(failure.isFailure());
        assertEquals("error", failure.recover(error -> Result.success(error)).toOptional().orElse(null));
    }

    @Test
    void shouldChainResultsAndStopAtTheFirstFailure() {
        var chained = Result.<Integer, String>success(5)
                .flatMap(value -> Result.<Integer, String>success(value + 1))
                .flatMap(value -> Result.<Integer, String>failure("stopped at " + value))
                .flatMap(value -> Result.<Integer, String>success(value + 100));

        var failure = assertInstanceOf(Result.Failure.class, chained);

        assertEquals("stopped at 6", failure.error());
    }

    @Test
    void shouldMapTheErrorOfAFailureAndKeepASuccess() {
        var failure = Result.<Integer, String>failure("error").mapError(String::length);
        var success = Result.<Integer, String>success(5).mapError(String::length);

        assertEquals(Optional.of(5), failure.recover(error -> Result.success(error)).toOptional());
        assertEquals(5, success.getOrElse(0));
    }

    @Test
    void shouldRecoverFromAFailureAndKeepASuccess() {
        var recovered = Result.<Integer, String>failure("error").recover(error -> Result.success(0));
        var untouched = Result.<Integer, String>success(5).recover(error -> Result.success(0));

        assertEquals(0, recovered.getOrElse(-1));
        assertEquals(5, untouched.getOrElse(-1));
    }
}
