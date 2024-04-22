package com.outbox.data.mapper;

import com.outbox.data.OutboxKafkaMessage;
import org.springframework.core.Ordered;

public interface OutboxEntityMapper<E> {

    OutboxKafkaMessage map(E domainEvent);

    default Integer order() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
