package com.hotel.infrastructure.producer.mapper;

import com.hotel.core.domain.utils.EventHelper;
import com.hotel.domain.avro.v1.HotelDeleted;
import com.hotel.domain.event.HotelDomainEvent.HotelDeletedEvent;
import com.outbox.data.OutboxKafkaMessage;
import com.outbox.data.mapper.OutboxEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HotelDeletedOutboxMapper implements OutboxEntityMapper<HotelDeletedEvent> {

    private final HotelEventMapper eventMapper;

    @Override
    public OutboxKafkaMessage map(final HotelDeletedEvent domainEvent) {
        final HotelDeleted outboxEvent = this.eventMapper.mapHotelDeleted(domainEvent);

        final Map<String, Object> headers = new HashMap<>();
        headers.put(EventHelper.EVENT_TYPE, domainEvent.getClass().getSimpleName());
        return new OutboxKafkaMessage(outboxEvent, headers);
    }
}
