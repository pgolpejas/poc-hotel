package com.reservation.domain.model;

import com.hotel.core.domain.ddd.ValueObject;
import com.hotel.core.domain.utils.ValidationUtils;

import java.io.Serializable;
import java.util.UUID;

public record GuestId(UUID value) implements ValueObject, Serializable {

  public GuestId {
    ValidationUtils.notNull(value, "GuestId can not be null: " + value);
  }
}
