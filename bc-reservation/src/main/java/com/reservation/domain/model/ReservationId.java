package com.reservation.domain.model;

import com.hotel.core.domain.ddd.ValueObject;
import com.hotel.core.domain.utils.ValidationUtils;

import java.io.Serializable;
import java.util.UUID;

public record ReservationId(UUID value) implements ValueObject, Serializable {

  public ReservationId {
    ValidationUtils.notNull(value, "ReservationId can not be null: " + value);
  }
}
