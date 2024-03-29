package com.reservation.configuration.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.reservation.application.controller.ReservationController;
import com.reservation.application.dto.ReservationDto;
import com.reservation.domain.core.DomainError;
import com.reservation.domain.exception.ReservationConflictException;
import com.reservation.domain.exception.ReservationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LOCAL_DATE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControllerExceptionHandlerTest {
    final ControllerExceptionHandler controllerExceptionHandler = new ControllerExceptionHandler();

    @Mock
    private HttpServletRequest request;
    private WebRequest webRequest;

    private static final String PATH = "/path";

    private static final String SERVLET = "/servlet";

    @BeforeEach
    void setUp() {
        when(this.request.getContextPath()).thenReturn(PATH);
        when(this.request.getServletPath()).thenReturn(SERVLET);
    }

    @Test
    void when_capture_DomainError_should_return_error() {

        final String detail = "DETAIL";

        final ResponseEntity<ProblemDetail> response =
                this.controllerExceptionHandler.handleDomainError(this.request, new DomainError(detail));

        this.validateResponse(response, HttpStatus.BAD_REQUEST, detail);
    }

    @Test
    void when_capture_ConflictException_should_return_error() {

        final String detail = "DETAIL";

        final ResponseEntity<ProblemDetail> response =
                this.controllerExceptionHandler.handleConflictException(this.request, new ReservationConflictException(detail) {
                });

        this.validateResponse(response, HttpStatus.CONFLICT, detail);
    }

    @Test
    void when_capture_NotFound_should_return_error() {

        final String detail = "DETAIL";

        final ResponseEntity<ProblemDetail> response =
                this.controllerExceptionHandler.handleNotFoundException(this.request, new ReservationNotFoundException(detail));

        this.validateResponse(response, HttpStatus.NOT_FOUND, detail);
    }

    @Test
    void when_capture_MethodArgumentNotValidException_should_return_error() {

        final BindingResult mockBindingResult = mock(BindingResult.class);
        final MethodArgumentNotValidException exception = new MethodArgumentNotValidException(mock(MethodParameter.class), mockBindingResult);

        when(mockBindingResult.getFieldErrors()).thenReturn(
                List.of(
                        new FieldError("ApiDTO", "param1", "not valid"),
                        new FieldError("ApiDTO", "param2", "missing")));
        when(mockBindingResult.getObjectName()).thenReturn("ApiDTO");
        when(mockBindingResult.getErrorCount()).thenReturn(2);

        final ResponseEntity<ProblemDetail> response =
                this.controllerExceptionHandler.handleMethodArgumentNotValidException(this.request, exception);

        this.validateResponse(response, HttpStatus.BAD_REQUEST, "Validation failed for ApiDTO. Errors [2]: param1 not valid, param2 missing");
    }
    @Test
    void when_capture_ConversionFailedException_should_return_error() throws NoSuchMethodException {

        final ReservationDto value = null;
        final Class<?> requiredType = LOCAL_DATE.getClass();
        final String name = "fromDate";
        final MethodParameter param = new MethodParameter(ReservationController.class.getMethod("getReservation", UUID.class), 0);
        final Throwable cause = new NullPointerException();
        final MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(value, requiredType, name, param, cause);

        final ResponseEntity<ProblemDetail> response =
                this.controllerExceptionHandler.handleConversionFailedException(this.request, exception);

        this.validateResponse(response, HttpStatus.BAD_REQUEST, "fromDate: null");
    }

    @Test
    void when_capture_invalid_format_exception_should_return_error() {

        final String detail = "message";
        final InvalidFormatException mockException = mock(InvalidFormatException.class);
        when(mockException.getMessage()).thenReturn("message");
        final ResponseEntity<ProblemDetail> response =
                this.controllerExceptionHandler.handleInvalidFormatException(this.request, mockException);

        this.validateResponse(response, HttpStatus.BAD_REQUEST, detail);
    }

    private void validateResponse(final ResponseEntity<ProblemDetail> response, final HttpStatus expectedStatus,
                                  final String expectedDetails) {
        assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(expectedStatus.value());
        assertThat(response.getBody().getTitle()).isEqualTo(expectedStatus.getReasonPhrase());
        assertThat(response.getBody().getDetail()).isEqualTo(expectedDetails);
        assertThat(response.getBody().getInstance()).isNotNull();
        assertThat(response.getBody().getInstance().getRawPath()).isEqualTo(PATH + SERVLET);
        assertThat(response.getBody().getType().toString()).isEqualTo("about:blank");
    }
}
