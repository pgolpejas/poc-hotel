package com.reservation.domain.model;

import com.hotel.core.domain.ddd.ValueObject;
import com.hotel.core.domain.utils.ValidationUtils;

import java.io.Serializable;
import java.util.UUID;

public record HotelId(UUID value) implements ValueObject, Serializable {

  public HotelId {
    ValidationUtils.notNull(value, "HotelId can not be null: " + value);
  }
}
