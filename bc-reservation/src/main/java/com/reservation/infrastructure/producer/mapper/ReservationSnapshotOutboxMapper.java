package com.reservation.infrastructure.producer.mapper;

import java.util.HashMap;
import java.util.Map;

import com.hotel.core.domain.ddd.AggregateRoot;
import com.hotel.core.domain.ddd.DomainEvent;
import com.hotel.core.domain.utils.EventHelper;
import com.outbox.data.OutboxKafkaMessage;
import com.outbox.data.mapper.OutboxEntityMapper;
import com.reservation.domain.avro.v1.ReservationSnapshot;
import com.reservation.domain.model.Reservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationSnapshotOutboxMapper implements OutboxEntityMapper<Reservation> {

    private final ReservationEventMapper eventMapper;

    @Override
    public OutboxKafkaMessage map(final Reservation dataEvent) {
        final ReservationSnapshot outboxEvent = this.eventMapper.mapReservation(dataEvent);
        return new OutboxKafkaMessage(outboxEvent, buildHeaders(dataEvent));
    }

    public Map<String, Object> buildHeaders(final AggregateRoot aggregateRoot) {
        final Map<String, Object> specificHeaders = new HashMap<>();
        specificHeaders.put(EventHelper.EVENT_DOMAIN_TYPE, getAction(aggregateRoot));
        specificHeaders.put(EventHelper.EVENT_TYPE, aggregateRoot.getClass().getSimpleName());
        return specificHeaders;
    }

    private static String getAction(final AggregateRoot aggregateRoot) {
        return aggregateRoot.domainEvents().stream().findFirst().map(DomainEvent::getActionType)
                .orElse(aggregateRoot.getClass().getSimpleName() + "Unknown");
    }
}
