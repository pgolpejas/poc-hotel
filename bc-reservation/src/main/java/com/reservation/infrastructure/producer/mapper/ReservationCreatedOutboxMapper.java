package com.reservation.infrastructure.producer.mapper;

import com.outbox.data.OutboxKafkaMessage;
import com.outbox.data.mapper.OutboxEntityMapper;
import com.reservation.domain.avro.v1.ReservationCreated;
import com.reservation.domain.event.ReservationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCreatedOutboxMapper implements OutboxEntityMapper<ReservationCreatedEvent> {

    private final ReservationEventMapper reservationEventMapper;

    @Override
    public OutboxKafkaMessage map(final ReservationCreatedEvent domainEvent) {
        final ReservationCreated outboxEvent = this.reservationEventMapper.mapReservationCreated(domainEvent);
        return new OutboxKafkaMessage(outboxEvent, new HashMap<>());
    }
}
