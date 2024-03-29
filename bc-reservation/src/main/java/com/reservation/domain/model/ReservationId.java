package com.reservation.domain.model;

import com.reservation.domain.core.ValueObject;
import com.reservation.domain.utils.ValidationUtils;

import java.io.Serializable;
import java.util.UUID;

public record ReservationId(UUID value) implements ValueObject, Serializable {

  public ReservationId {
    ValidationUtils.notNull(value, "ReservationId can not be null: " + value);
  }
}
