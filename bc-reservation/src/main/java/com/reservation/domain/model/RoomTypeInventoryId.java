package com.reservation.domain.model;

import java.io.Serializable;
import java.util.UUID;

import com.hotel.core.domain.ddd.ValueObject;
import com.hotel.core.domain.utils.ValidationUtils;

public record RoomTypeInventoryId(UUID value) implements ValueObject, Serializable {

  public RoomTypeInventoryId {
    ValidationUtils.notNull(value, "RoomTypeInventoryId can not be null: " + value);
  }
}
