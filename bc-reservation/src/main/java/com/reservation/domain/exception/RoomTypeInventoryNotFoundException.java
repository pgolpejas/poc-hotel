package com.reservation.domain.exception;

import com.hotel.core.domain.exception.NotFoundException;

import java.io.Serial;

public class RoomTypeInventoryNotFoundException extends NotFoundException {

    @Serial
    private static final long serialVersionUID = 1L;

    public RoomTypeInventoryNotFoundException(final String detail) {
        super(detail);
    }

    public RoomTypeInventoryNotFoundException(final String detailToFormat, Object ...arguments) {
        super(detailToFormat, arguments);
    }
}
