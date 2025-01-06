package com.hotel.core.domain.exception;

import java.io.Serial;

import lombok.Getter;

@Getter
public class CustomValidationException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;

  private final String detail;

  public CustomValidationException(final String detail) {
    super(detail);
    this.detail = detail;
  }

  protected CustomValidationException(final String detail, Object... arguments) {
    super(String.format(detail, arguments));
    this.detail = super.getMessage();
  }
}
