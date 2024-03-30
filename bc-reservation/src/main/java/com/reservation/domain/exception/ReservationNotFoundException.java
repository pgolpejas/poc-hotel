package com.reservation.domain.exception;

import com.hotel.core.domain.exception.NotFoundException;

import java.io.Serial;

public class ReservationNotFoundException extends NotFoundException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ReservationNotFoundException(final String detail) {
        super(detail);
    }
}
