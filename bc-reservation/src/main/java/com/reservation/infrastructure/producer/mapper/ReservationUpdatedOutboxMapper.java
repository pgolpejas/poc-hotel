package com.reservation.infrastructure.producer.mapper;

import com.hotel.core.domain.utils.EventHelper;
import com.outbox.data.OutboxKafkaMessage;
import com.outbox.data.mapper.OutboxEntityMapper;
import com.reservation.domain.avro.v1.ReservationUpdated;
import com.reservation.domain.event.ReservationDomainEvent.ReservationUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationUpdatedOutboxMapper implements OutboxEntityMapper<ReservationUpdatedEvent> {

    private final ReservationEventMapper eventMapper;

    @Override
    public OutboxKafkaMessage map(final ReservationUpdatedEvent domainEvent) {
        final ReservationUpdated outboxEvent = this.eventMapper.mapReservationUpdated(domainEvent);

        final Map<String, Object> headers = new HashMap<>();
        headers.put(EventHelper.EVENT_TYPE, domainEvent.getClass().getSimpleName());
        return new OutboxKafkaMessage(outboxEvent, headers);
    }
}
