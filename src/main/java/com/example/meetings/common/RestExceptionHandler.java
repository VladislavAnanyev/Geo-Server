package com.example.meetings.common;

import com.example.meetings.common.exception.*;
import com.example.meetings.common.model.ErrorResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.example.meetings.util.Constants.EXCEPTION_INTERNAL_SERVER;
import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleEntityNotFoundEx(EntityNotFoundException ex, Locale locale) {
        logClientError(ex);
        return ResponseEntity.status(NOT_FOUND).body(errorResponse(resolveMessage(ex.getMessage(), locale)));
    }

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex, Locale locale) {
        logClientError(ex);
        ApiError apiError = new ApiError();
        apiError.setMessage(resolveMessage(ex.getMessage(), locale));
        apiError.setCode(ex.getCode());
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(apiError);
        return new ResponseEntity<>(errorResponse, NOT_FOUND);
    }

    @NotNull
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        logClientError(ex);
        return ResponseEntity.status(BAD_REQUEST)
                .body(errorResponse(resolveMessage("exception.request.body.invalid", request.getLocale())));
    }

    @NotNull
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
                                                                   HttpStatus status, WebRequest request) {
        logClientError(ex);
        return ResponseEntity.status(status)
                .body(errorResponse(resolveMessage("exception.handler.not.found", request.getLocale())));
    }

    @NotNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        Locale locale = request.getLocale();
        logClientError(ex);
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .map(message -> resolveValidationMessage(message, locale))
                .collect(Collectors.toList());

        ApiError apiError = new ApiError(resolveMessage("exception.bad.request", locale), errors);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(apiError);
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleSecurityException(SecurityException ex, Locale locale) {
        logClientError(ex);
        return ResponseEntity.status(FORBIDDEN).body(errorResponse(resolveMessage(ex.getMessage(), locale)));
    }

    @ExceptionHandler(SubscriptionRequiredException.class)
    public ResponseEntity<ErrorResponse> handleSubscriptionRequired(SubscriptionRequiredException ex, Locale locale) {
        logClientError(ex);
        ErrorResponse errorResponse = new ErrorResponse();
        ApiError apiError = new ApiError();
        apiError.setMessage(resolveMessage(ex.getMessage(), locale));
        apiError.setCode(ex.getCode());
        errorResponse.setError(apiError);
        return new ResponseEntity<>(errorResponse, PAYMENT_REQUIRED);
    }

    @ExceptionHandler(LogicException.class)
    public ResponseEntity<ErrorResponse> handleLogicException(LogicException ex, Locale locale) {
        logClientError(ex);
        return ResponseEntity.status(BAD_REQUEST).body(errorResponse(resolveMessage(ex.getMessage(), locale)));
    }

    @ExceptionHandler(GlobalException.class)
    protected ResponseEntity<ErrorResponse> handleGlobalException(GlobalException ex, Locale locale) {
        logClientError(ex);
        ErrorResponse errorResponse = new ErrorResponse();
        ApiError apiError = new ApiError();
        apiError.setMessage(resolveMessage(ex.getMessage(), locale));
        apiError.setCode(ex.getCode());
        errorResponse.setError(apiError);
        return ResponseEntity.ok().body(errorResponse);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorResponse> handleStorageException(StorageException ex, Locale locale) {
        logServerError(ex);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(errorResponse(resolveMessage(ex.getMessage(), locale)));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex, Locale locale) {
        logServerError(ex);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(errorResponse(resolveMessage(EXCEPTION_INTERNAL_SERVER, locale)));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, Locale locale) {
        logClientError(ex);
        String message = resolveMessage(
                "exception.parameter.type.mismatch",
                locale,
                ex.getName(),
                ex.getValue(),
                requireNonNull(ex.getRequiredType()).getSimpleName());
        return ResponseEntity.status(BAD_REQUEST).body(errorResponse(message));
    }

    @ExceptionHandler(ListenerExecutionFailedException.class)
    protected ResponseEntity<ErrorResponse> handleAmqpFailure(ListenerExecutionFailedException ex, Locale locale) {
        logServerError(ex);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(errorResponse(resolveMessage("exception.rabbit.internal", locale)));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex, Locale locale) {
        if (ex.getStatus().is5xxServerError()) {
            logServerError(ex);
        } else {
            logClientError(ex);
        }
        String reason = ex.getReason();
        String message = reason == null || reason.isBlank()
                ? resolveMessage("exception.bad.request", locale)
                : resolveMessage(reason, locale);
        return ResponseEntity.status(ex.getStatus()).body(errorResponse(message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, Locale locale) {
        logServerError(ex);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(errorResponse(resolveMessage(EXCEPTION_INTERNAL_SERVER, locale)));
    }

    private ErrorResponse errorResponse(String message) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(new ApiError(message));
        return errorResponse;
    }

    private String resolveMessage(@Nullable String key, Locale locale, Object... args) {
        if (key == null || key.isBlank()) {
            return resolveMessage(EXCEPTION_INTERNAL_SERVER, locale);
        }
        try {
            return messageSource.getMessage(key, args, locale);
        } catch (NoSuchMessageException ex) {
            return key;
        }
    }

    private String resolveValidationMessage(@Nullable String message, Locale locale) {
        if ("must not be blank".equals(message)) {
            return resolveMessage("validation.not.blank", locale);
        }
        if ("must not be null".equals(message)) {
            return resolveMessage("validation.not.null", locale);
        }
        return resolveMessage(message, locale);
    }

    private void logClientError(Exception ex) {
        logger.warn("Client error: " + ex.getMessage(), ex);
    }

    private void logServerError(Exception ex) {
        logger.error("Server error", ex);
    }
}
