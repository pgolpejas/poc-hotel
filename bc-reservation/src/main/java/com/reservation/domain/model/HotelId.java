package com.reservation.domain.model;

import com.reservation.domain.core.ValueObject;
import com.reservation.domain.utils.ValidationUtils;

import java.io.Serializable;
import java.util.UUID;

public record HotelId(UUID value) implements ValueObject, Serializable {

  public HotelId {
    ValidationUtils.notNull(value, "HotelId can not be null: " + value);
  }
}
