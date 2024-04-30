package com.reservation.domain.exception;

import com.hotel.core.domain.exception.ConflictException;

import java.io.Serial;

public class RoomTypeInventoryConflictException extends ConflictException {

  @Serial
  private static final long serialVersionUID = 1L;

  public RoomTypeInventoryConflictException(final String detail) {
    super(detail);
  }

  public RoomTypeInventoryConflictException(final String detailToFormat, Object ...arguments) {
    super(detailToFormat, arguments);
  }
}
