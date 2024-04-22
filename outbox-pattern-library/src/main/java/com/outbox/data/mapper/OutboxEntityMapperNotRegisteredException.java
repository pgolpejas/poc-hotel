package com.outbox.data.mapper;

import java.io.Serial;

public class OutboxEntityMapperNotRegisteredException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -6712989531109077172L;

    public OutboxEntityMapperNotRegisteredException(Class<?> clazz) {
        super("Mapper not registered for the class: " + clazz.getName());
    }

}
