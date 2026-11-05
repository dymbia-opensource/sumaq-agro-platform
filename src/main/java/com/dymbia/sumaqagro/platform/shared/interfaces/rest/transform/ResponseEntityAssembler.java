package com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.function.Function;

/**
 * Assembler that converts the outcome of an application service into an HTTP response.
 * Controllers use it so they do not repeat how a success or an error becomes a response.
 *
 * @since 1.0
 */
public final class ResponseEntityAssembler {

    private ResponseEntityAssembler() {
    }

    /**
     * Converts a {@link Result} into a response. A success becomes the resource with the given status;
     * a failure becomes the problem detail of its {@link ApplicationError}.
     *
     * @param result                   outcome of a command service
     * @param successResourceAssembler converts the value of a success into its resource
     * @param successStatus            HTTP status used when the result is a success
     * @param <T>                      type of the value of the result
     * @param <R>                      type of the resource
     * @return the HTTP response
     */
    public static <T, R> ResponseEntity<?> toResponseEntityFromResult(
            Result<T, ApplicationError> result,
            Function<T, R> successResourceAssembler,
            HttpStatusCode successStatus) {
        return switch (result) {
            case Result.Success<T, ApplicationError> success ->
                    new ResponseEntity<>(successResourceAssembler.apply(success.value()), successStatus);
            case Result.Failure<T, ApplicationError> failure ->
                    ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(failure.error());
        };
    }

    /**
     * Converts an optional into a response. A present value becomes {@code 200 OK} with its resource;
     * an empty optional becomes the problem detail of the given error, normally a not found error.
     *
     * @param optional                 outcome of a query service
     * @param successResourceAssembler converts the value into its resource
     * @param errorWhenEmpty           error returned when there is no value
     * @param <T>                      type of the value
     * @param <R>                      type of the resource
     * @return the HTTP response
     */
    public static <T, R> ResponseEntity<?> toResponseEntityFromOptional(
            Optional<T> optional,
            Function<T, R> successResourceAssembler,
            ApplicationError errorWhenEmpty) {
        return optional.<ResponseEntity<?>>map(value -> ResponseEntity.ok(successResourceAssembler.apply(value)))
                .orElseGet(() -> ProblemDetailFromApplicationErrorAssembler
                        .toResponseFromApplicationError(errorWhenEmpty));
    }
}
