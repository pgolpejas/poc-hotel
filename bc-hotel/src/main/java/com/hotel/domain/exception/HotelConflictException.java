package com.hotel.domain.exception;

import com.hotel.core.domain.exception.ConflictException;

import java.io.Serial;

public class HotelConflictException extends ConflictException {

    @Serial
    private static final long serialVersionUID = 1L;

    public HotelConflictException(final String detail) {
        super(detail);
    }

    public HotelConflictException(final String detailToFormat, Object... arguments) {
        super(detailToFormat, arguments);
    }
}
