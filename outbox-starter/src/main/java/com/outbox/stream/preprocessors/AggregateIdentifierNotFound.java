package com.outbox.stream.preprocessors;

import java.io.Serial;

public class AggregateIdentifierNotFound extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -2445235935188385301L;

    public AggregateIdentifierNotFound(String message) {
        super(message);
    }

    public AggregateIdentifierNotFound(String message, Throwable error) {
        super(message, error);
    }

}
