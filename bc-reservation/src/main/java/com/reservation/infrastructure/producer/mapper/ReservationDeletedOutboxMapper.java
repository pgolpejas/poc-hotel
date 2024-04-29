package com.reservation.infrastructure.producer.mapper;

import com.hotel.core.domain.utils.EventHelper;
import com.outbox.data.OutboxKafkaMessage;
import com.outbox.data.mapper.OutboxEntityMapper;
import com.reservation.domain.avro.v1.ReservationDeleted;
import com.reservation.domain.event.ReservationDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationDeletedOutboxMapper implements OutboxEntityMapper<ReservationDeletedEvent> {

    private final ReservationEventMapper reservationEventMapper;

    @Override
    public OutboxKafkaMessage map(final ReservationDeletedEvent domainEvent) {
        final ReservationDeleted outboxEvent = this.reservationEventMapper.mapReservationDeleted(domainEvent);

        final Map<String, Object> headers = new HashMap<>();
        headers.put(EventHelper.EVENT_TYPE, domainEvent.getClass().getSimpleName());
        return new OutboxKafkaMessage(outboxEvent, headers);
    }
}
