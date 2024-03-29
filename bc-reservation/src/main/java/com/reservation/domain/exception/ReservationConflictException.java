package com.reservation.domain.exception;

import java.io.Serial;

public class ReservationConflictException extends ConflictException {

  @Serial
  private static final long serialVersionUID = 1L;

  public ReservationConflictException(final String detail) {
    super(detail);
  }
}
