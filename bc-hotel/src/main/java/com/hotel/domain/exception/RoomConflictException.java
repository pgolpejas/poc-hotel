package com.hotel.domain.exception;

import com.hotel.core.domain.exception.ConflictException;

import java.io.Serial;

public class RoomConflictException extends ConflictException {

    @Serial
    private static final long serialVersionUID = 1L;

    public RoomConflictException(final String detail) {
        super(detail);
    }

    public RoomConflictException(final String detailToFormat, Object... arguments) {
        super(detailToFormat, arguments);
    }
}
