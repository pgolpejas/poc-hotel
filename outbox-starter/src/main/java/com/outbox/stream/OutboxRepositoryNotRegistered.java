package com.outbox.stream;

public class OutboxRepositoryNotRegistered extends RuntimeException {

    public OutboxRepositoryNotRegistered(@SuppressWarnings("rawtypes") Class clazz) {
        super("OutboxRepositoryNotRegistered for class:" + clazz);
    }

}
