package com.reservation.infrastructure.producer.mapper;

import com.outbox.data.OutboxKafkaMessage;
import com.outbox.data.mapper.OutboxEntityMapper;
import com.reservation.domain.avro.v1.ReservationDeleted;
import com.reservation.domain.event.ReservationDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationDeletedOutboxMapper implements OutboxEntityMapper<ReservationDeletedEvent> {

    private final ReservationEventMapper reservationEventMapper;

    @Override
    public OutboxKafkaMessage map(final ReservationDeletedEvent domainEvent) {
        final ReservationDeleted outboxEvent = this.reservationEventMapper.mapReservationDeleted(domainEvent);
        return new OutboxKafkaMessage(outboxEvent, new HashMap<>());
    }
}
