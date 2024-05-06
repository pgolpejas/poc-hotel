package com.outbox.data.mapper;

import java.io.Serial;

public class OutboxEntityMapperSerializingException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -2445235935188385301L;

    public OutboxEntityMapperSerializingException(String message) {
        super(message);
    }

}
