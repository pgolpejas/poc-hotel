package com.hotel.core.application.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.hotel.core.domain.ddd.DomainError;
import com.hotel.core.domain.exception.ConflictException;
import com.hotel.core.domain.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.Optional;

@SuppressWarnings("ALL")
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ProblemDetail> handleConflictException(final HttpServletRequest request, final ConflictException exception) {
        final HttpStatus status = HttpStatus.CONFLICT;
        return ResponseEntity.status(status)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(this.build(status, exception.getDetail(), request.getContextPath() + request.getServletPath()));
    }
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFoundException(final HttpServletRequest request, final NotFoundException exception) {
        final HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(this.build(status, exception.getDetail(), request.getContextPath() + request.getServletPath()));
    }


    @ExceptionHandler(DomainError.class)
    public ResponseEntity<ProblemDetail> handleDomainError(final HttpServletRequest request, final DomainError exception) {
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(this.build(status, exception.getMessage(), request.getContextPath() + request.getServletPath()));
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ProblemDetail> handleConversionFailedException(final HttpServletRequest request,
                                                                          final MethodArgumentTypeMismatchException exception) {
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        final Exception cause = (Exception) this.getRootCause(exception);
        final String detailMessage = exception.getPropertyName() + ": " + cause.getMessage();

        return ResponseEntity.status(status)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(this.build(status, detailMessage, request.getContextPath() + request.getServletPath()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        ResponseEntity<Object> response = super.handleMethodArgumentNotValid(ex, headers, status, request);

        if (null != response && response.getBody() instanceof ProblemDetail problemDetailBody) {
            problemDetailBody.setProperty("message", ex.getMessage());
            BindingResult result = ex.getBindingResult();
            problemDetailBody.setProperty("message",
                    "Validation failed for object='" + result.getObjectName() + "'. " + "Error count: " + result.getErrorCount());
            problemDetailBody.setProperty("errors", result.getAllErrors());
        }
        return response;
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ProblemDetail> handleInvalidFormatException(final HttpServletRequest request,
                                                                       final InvalidFormatException exception) {
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(this.build(status, exception.getMessage(), request.getContextPath() + request.getServletPath()));
    }


    private Throwable getRootCause(final Exception exception) {
        return Optional.ofNullable(ExceptionUtils.getRootCause(exception)).orElse(exception);
    }
    
    private ProblemDetail build(final HttpStatus httpStatus, final String detail, final String instance) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, detail);
        problemDetail.setInstance(URI.create(instance));
        return problemDetail;
    }
}
