package com.reservation.infrastructure.producer.mapper;

import com.hotel.core.domain.utils.EventHelper;
import com.outbox.data.OutboxKafkaMessage;
import com.outbox.data.mapper.OutboxEntityMapper;
import com.reservation.domain.avro.v1.ReservationCreated;
import com.reservation.domain.event.ReservationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCreatedOutboxMapper implements OutboxEntityMapper<ReservationCreatedEvent> {

    private final ReservationEventMapper eventMapper;

    @Override
    public OutboxKafkaMessage map(final ReservationCreatedEvent domainEvent) {
        final ReservationCreated outboxEvent = this.eventMapper.mapReservationCreated(domainEvent);

        final Map<String, Object> headers = new HashMap<>();
        headers.put(EventHelper.EVENT_TYPE, domainEvent.getClass().getSimpleName());
        return new OutboxKafkaMessage(outboxEvent, headers);
    }
}
