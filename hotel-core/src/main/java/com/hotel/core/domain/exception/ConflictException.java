package com.hotel.core.domain.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public abstract class ConflictException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String detail;

    protected ConflictException(final String detail) {
        super(detail);
        this.detail = detail;
    }

    protected ConflictException(final String detail, Object ...arguments) {
        super(String.format(detail, arguments));
        this.detail = super.getMessage();
    }

}
