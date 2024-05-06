package com.hotel.domain.model;

import com.hotel.core.domain.ddd.ValueObject;
import com.hotel.core.domain.utils.ValidationUtils;

import java.io.Serializable;
import java.util.UUID;

public record RoomId(UUID value) implements ValueObject, Serializable {

    public RoomId {
        ValidationUtils.notNull(value, "RoomId can not be null: " + value);
    }
}
