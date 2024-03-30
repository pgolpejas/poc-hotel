package com.hotel.core.domain.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class NotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String detail;

    public NotFoundException(final String detail) {
        super(detail);
        this.detail = detail;
    }

}
