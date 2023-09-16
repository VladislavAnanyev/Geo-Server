package com.example.meetings.common;

import com.example.meetings.common.exception.ApiError;
import com.example.meetings.common.exception.GlobalException;
import com.example.meetings.common.exception.LogicException;
import com.example.meetings.common.model.ErrorResponse;
import com.example.meetings.common.exception.UserNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFoundEx(EntityNotFoundException ex) {
        ApiError apiError = new ApiError(ex.getMessage());
        return new ResponseEntity<>(apiError, NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, Locale locale) {
        ApiError apiError = new ApiError();
        apiError.setMessage(messageSource.getMessage(ex.getMessage(), null, locale));
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(apiError);
        return new ResponseEntity<>(errorResponse, OK);
    }

    @NotNull
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError apiError = new ApiError(ex.getMessage());
        return new ResponseEntity<>(apiError, status);
    }

    @NotNull
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
                                                                   HttpStatus status, WebRequest request) {
        return new ResponseEntity<Object>(new ApiError(ex.getMessage()), status);
    }

    @NotNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        ApiError apiError = new ApiError(ex.getMessage(), errors);
        return new ResponseEntity<>(apiError, status);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Object> handleException(SecurityException e) {
        ApiError apiError = new ApiError(e.getMessage());
        return new ResponseEntity<>(apiError, FORBIDDEN);
    }

    @ExceptionHandler(LogicException.class)
    public ResponseEntity<Object> handleException(LogicException e, Locale locale) {

        ErrorResponse errorResponse = new ErrorResponse();
        ApiError apiError = new ApiError();
        apiError.setMessage(messageSource.getMessage(e.getMessage(), null, locale));
        errorResponse.setError(apiError);

        return new ResponseEntity<>(errorResponse, OK);
    }

    @ExceptionHandler(GlobalException.class)
    protected ResponseEntity<Object> handleGlobalException (GlobalException e, Locale locale) {
        ErrorResponse errorResponse = new ErrorResponse();
        ApiError apiError = new ApiError();
        apiError.setMessage(messageSource.getMessage(e.getMessage(), null, locale));
        errorResponse.setError(apiError);

        return ResponseEntity
                .ok()
                .body(errorResponse);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> handleException(IOException e) {
        ApiError apiError = new ApiError(e.getMessage());
        return new ResponseEntity<>(apiError, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ApiError apiError = new ApiError();
        apiError.setMessage(String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",
                ex.getName(), ex.getValue(), requireNonNull(ex.getRequiredType()).getSimpleName()));

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(apiError);

        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    @ExceptionHandler(ListenerExecutionFailedException.class)
    protected ResponseEntity<Object> handleAmqpFailure(ListenerExecutionFailedException e) {
        ApiError apiError = new ApiError(e.getMessage());
        return new ResponseEntity<>(apiError, BAD_REQUEST);
    }

}
