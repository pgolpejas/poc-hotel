package com.hotel.domain.exception;

import com.hotel.core.domain.exception.NotFoundException;

import java.io.Serial;

public class RoomNotFoundException extends NotFoundException {

    @Serial
    private static final long serialVersionUID = 1L;

    public RoomNotFoundException(final String detail) {
        super(detail);
    }

    public RoomNotFoundException(final String detailToFormat, Object... arguments) {
        super(detailToFormat, arguments);
    }
}
