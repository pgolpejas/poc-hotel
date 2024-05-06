package com.hotel.domain.exception;

import com.hotel.core.domain.exception.NotFoundException;

import java.io.Serial;

public class HotelNotFoundException extends NotFoundException {

    @Serial
    private static final long serialVersionUID = 1L;

    public HotelNotFoundException(final String detail) {
        super(detail);
    }

    public HotelNotFoundException(final String detailToFormat, Object... arguments) {
        super(detailToFormat, arguments);
    }
}
