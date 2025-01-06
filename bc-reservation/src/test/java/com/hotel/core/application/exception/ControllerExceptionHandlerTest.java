package com.hotel.core.application.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LOCAL_DATE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.UUID;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.hotel.core.domain.ddd.DomainError;
import com.reservation.domain.exception.ReservationConflictException;
import com.reservation.domain.exception.ReservationNotFoundException;
import com.reservation.infrastructure.controller.ReservationController;
import com.reservation.openapi.model.ReservationDto;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ExtendWith(MockitoExtension.class)
class ControllerExceptionHandlerTest {
  final ControllerExceptionHandler controllerExceptionHandler = new ControllerExceptionHandler();

  @Mock
  private HttpServletRequest request;

  @Mock
  private WebRequest webRequest;

  private static final String PATH = "/path";

  private static final String SERVLET = "/servlet";

  @Nested
  class MethodArgumentNotValidExceptionTest {

    @Test
    void when_capture_MethodArgumentNotValidException_should_return_error() {

      final BindingResult mockBindingResult = mock(BindingResult.class);
      final Method mockMethod = mock(Method.class);
      final MethodParameter methodParameter = new MethodParameter(mockMethod, -1);
      final MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, mockBindingResult);

      final ResponseEntity<Object> response =
          ControllerExceptionHandlerTest.this.controllerExceptionHandler.handleMethodArgumentNotValid(exception, null,
              HttpStatus.BAD_REQUEST,
              ControllerExceptionHandlerTest.this.webRequest);

      assertThat(response).isNotNull();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
      assertThat(response.getBody()).isNotNull();
    }
  }

  @Nested
  class OtherExceptions {
    @BeforeEach
        void setUp() {
            when(ControllerExceptionHandlerTest.this.request.getContextPath()).thenReturn(PATH);
            when(ControllerExceptionHandlerTest.this.request.getServletPath()).thenReturn(SERVLET);
        }

    @Test
    void when_capture_DomainError_should_return_error() {

      final String detail = "DETAIL";

      final ResponseEntity<ProblemDetail> response =
          ControllerExceptionHandlerTest.this.controllerExceptionHandler.handleDomainError(ControllerExceptionHandlerTest.this.request,
              new DomainError(detail));

      this.validateResponse(response, HttpStatus.BAD_REQUEST, detail);
    }

    @Test
    void when_capture_ConflictException_should_return_error() {

      final String detail = "DETAIL";

      final ResponseEntity<ProblemDetail> response =
          ControllerExceptionHandlerTest.this.controllerExceptionHandler
              .handleConflictException(ControllerExceptionHandlerTest.this.request, new ReservationConflictException(detail) {});

      this.validateResponse(response, HttpStatus.CONFLICT, detail);
    }

    @Test
    void when_capture_NotFound_should_return_error() {

      final String detail = "DETAIL";

      final ResponseEntity<ProblemDetail> response =
          ControllerExceptionHandlerTest.this.controllerExceptionHandler
              .handleNotFoundException(ControllerExceptionHandlerTest.this.request, new ReservationNotFoundException(detail));

      this.validateResponse(response, HttpStatus.NOT_FOUND, detail);
    }

    @Test
    void when_capture_ConversionFailedException_should_return_error() throws NoSuchMethodException {

      final ReservationDto value = null;
      final Class<?> requiredType = LOCAL_DATE.getClass();
      final String name = "fromDate";
      final MethodParameter param = new MethodParameter(ReservationController.class.getMethod("getReservation", UUID.class), 0);
      final Throwable cause = new NullPointerException();
      final MethodArgumentTypeMismatchException exception =
          new MethodArgumentTypeMismatchException(value, requiredType, name, param, cause);

      final ResponseEntity<ProblemDetail> response =
          ControllerExceptionHandlerTest.this.controllerExceptionHandler
              .handleConversionFailedException(ControllerExceptionHandlerTest.this.request, exception);

      this.validateResponse(response, HttpStatus.BAD_REQUEST, "fromDate: null");
    }

    @Test
    void when_capture_invalid_format_exception_should_return_error() {

      final String detail = "message";
      final InvalidFormatException mockException = mock(InvalidFormatException.class);
      when(mockException.getMessage()).thenReturn("message");
      final ResponseEntity<ProblemDetail> response =
          ControllerExceptionHandlerTest.this.controllerExceptionHandler
              .handleInvalidFormatException(ControllerExceptionHandlerTest.this.request, mockException);

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
      assertThat(response.getBody().getType().toString()).hasToString("about:blank");
    }
  }
}
