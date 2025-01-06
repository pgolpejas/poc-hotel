package com.reservation.domain.exception;

import java.io.Serial;

import com.hotel.core.domain.exception.NotFoundException;

public class AggregatedReservationNotFoundException extends NotFoundException {

  @Serial
  private static final long serialVersionUID = 1L;

  public AggregatedReservationNotFoundException(final String detail) {
    super(detail);
  }

  public AggregatedReservationNotFoundException(final String detailToFormat, Object... arguments) {
    super(detailToFormat, arguments);
  }
}
