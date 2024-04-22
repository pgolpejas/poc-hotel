package com.reservation.infrastructure.producer.mapper;

import com.outbox.data.OutboxKafkaMessage;
import com.outbox.data.mapper.OutboxEntityMapper;
import com.reservation.domain.avro.v1.ReservationUpdated;
import com.reservation.domain.event.ReservationUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationUpdatedOutboxMapper implements OutboxEntityMapper<ReservationUpdatedEvent> {

    private final ReservationEventMapper reservationEventMapper;

    @Override
    public OutboxKafkaMessage map(final ReservationUpdatedEvent domainEvent) {
        final ReservationUpdated outboxEvent = this.reservationEventMapper.mapReservationUpdated(domainEvent);
        return new OutboxKafkaMessage(outboxEvent, new HashMap<>());
    }
}
