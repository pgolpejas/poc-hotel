package com.outbox.stream.preprocessors;

import java.io.Serial;

public class AggregateEventsNotFound extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -2445235935188385301L;

    public AggregateEventsNotFound(String message) {
        super(message);
    }

    public AggregateEventsNotFound(String message, Throwable error) {
        super(message, error);
    }

}
