package com.dymbia.sumaqagro.platform.shared.interfaces.rest;

import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.text.MessageFormat;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Global exception handler for REST API.
 * Provides centralized exception handling for the entire application,
 * ensuring all unhandled exceptions are translated to consistent
 * RFC 9457 ProblemDetail HTTP responses.
 * <p>
 * It extends {@link ResponseEntityExceptionHandler}, so the standard Spring MVC exceptions
 * keep their own status instead of becoming an internal server error: a malformed JSON body,
 * a path variable of the wrong type or a missing request parameter answer 400, an unsupported
 * method 405 and an unknown URL 404.
 * </p>
 */
@RestControllerAdvice
@NullMarked
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String MESSAGES_BASENAME = "messages";

    /**
     * Handles validation exceptions from Spring's request body validation.
     * Maps validation failure to a standardized RFC 9457 ProblemDetail response.
     *
     * @param ex      the validation exception from @Valid binding
     * @param headers the headers of the response
     * @param status  the status selected by Spring MVC
     * @param request the current request
     * @return error response with BAD_REQUEST status and detailed invalid parameters
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        var fieldErrors = ex.getBindingResult().getFieldErrors();
        var validationPrefix = resolveMessageOrDefault("validation.field.prefix", "Field");
        var errorDetails = fieldErrors.isEmpty()
                ? resolveMessageOrDefault("validation.request.failed", "Request validation failed")
                : fieldErrors.stream()
                        .map(error -> "%s %s: %s".formatted(
                                validationPrefix,
                                error.getField(),
                                error.getDefaultMessage()
                        ))
                        .reduce((a, b) -> a + "; " + b)
                        .orElse(resolveMessageOrDefault("validation.request.failed", "Request validation failed"));

        var invalidParams = fieldErrors.stream()
                .map(err -> Map.of(
                        "field", err.getField(),
                        "message", err.getDefaultMessage() != null ? err.getDefaultMessage() : "Invalid value"
                ))
                .toList();

        var applicationError = ApplicationError.validationError("request-body", errorDetails);
        var problemDetail = ProblemDetailFromApplicationErrorAssembler.toProblemDetailFromApplicationError(applicationError);
        if (!invalidParams.isEmpty()) {
            problemDetail.setProperty("invalidParams", invalidParams);
        }

        return ResponseEntity.status(problemDetail.getStatus()).headers(headers).body(problemDetail);
    }

    /**
     * Handles invalid request arguments such as malformed UUID path or payload values.
     *
     * @param ex the illegal argument exception
     * @return error response with BAD_REQUEST status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException ex) {
        var applicationError = ApplicationError.validationError(
                resolveMessageOrDefault("validation.request.argument", "request-argument"),
                ex.getMessage() != null ? resolveMessageOrDefault(ex.getMessage(), ex.getMessage()) : resolveMessageOrDefault("validation.request.failed", "Request validation failed")
        );
        return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(applicationError);
    }

    /**
     * Handles a request that reached a secured method without the required authority,
     * for example one rejected by {@code @PreAuthorize}.
     *
     * @param ex the access denied exception
     * @return error response with FORBIDDEN status
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDeniedException(AccessDeniedException ex) {
        var applicationError = new ApplicationError(
                "ACCESS_DENIED",
                "Access denied",
                ex.getMessage() != null ? ex.getMessage() : "Access denied"
        );
        return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(applicationError);
    }

    /**
     * Handles a request that reached a secured method without a valid authentication.
     *
     * @param ex the authentication exception
     * @return error response with UNAUTHORIZED status
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthenticationException(AuthenticationException ex) {
        var applicationError = new ApplicationError(
                "AUTHENTICATION_REQUIRED",
                "Authentication required",
                ex.getMessage() != null ? ex.getMessage() : "Authentication required"
        );
        return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(applicationError);
    }

    /**
     * Handles unexpected runtime exceptions not caught by specific handlers.
     * Maps to a generic unexpected error response.
     *
     * @param ex the unhandled runtime exception
     * @return error response with INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> handleRuntimeException(RuntimeException ex) {
        var applicationError = ApplicationError.unexpected(
                resolveMessageOrDefault("error.unexpected.context", "global-exception-handler"),
                ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred"
        );
        return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(applicationError);
    }

    /**
     * Handles all other exceptions not matched by specific handlers.
     * Provides a final fallback for any unexpected exception type.
     *
     * @param ex the exception
     * @return error response with INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleException(Exception ex) {
        var applicationError = ApplicationError.unexpected(
                resolveMessageOrDefault("error.unexpected.context", "global-exception-handler"),
                ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred"
        );
        return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(applicationError);
    }

    private String resolveMessageOrDefault(String key, String defaultValue, Object... args) {
        try {
            var bundle = ResourceBundle.getBundle(MESSAGES_BASENAME, LocaleContextHolder.getLocale(),
                    ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));
            if (!bundle.containsKey(key)) {
                return defaultValue;
            }
            return MessageFormat.format(bundle.getString(key), args);
        } catch (MissingResourceException ex) {
            return defaultValue;
        }
    }
}
