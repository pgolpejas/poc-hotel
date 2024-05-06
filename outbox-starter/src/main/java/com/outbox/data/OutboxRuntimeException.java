package com.outbox.data;

import java.io.Serial;

public class OutboxRuntimeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 2172964253187587815L;

    public OutboxRuntimeException(final String message, Throwable error) {
        super(message, error);
    }

    public OutboxRuntimeException(final String message) {
        super(message);
    }

}
