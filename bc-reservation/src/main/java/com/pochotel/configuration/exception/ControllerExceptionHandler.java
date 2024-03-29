package com.pocspringbootkafka.configuration.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

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
}