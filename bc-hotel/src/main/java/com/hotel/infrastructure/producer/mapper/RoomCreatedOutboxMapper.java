package com.hotel.infrastructure.producer.mapper;

import com.hotel.core.domain.utils.EventHelper;
import com.hotel.domain.event.RoomDomainEvent.RoomCreatedEvent;
import com.outbox.data.OutboxKafkaMessage;
import com.outbox.data.mapper.OutboxEntityMapper;
import com.room.domain.avro.v1.RoomCreated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomCreatedOutboxMapper implements OutboxEntityMapper<RoomCreatedEvent> {

    private final RoomEventMapper eventMapper;

    @Override
    public OutboxKafkaMessage map(final RoomCreatedEvent domainEvent) {
        final RoomCreated outboxEvent = this.eventMapper.mapRoomCreated(domainEvent);

        final Map<String, Object> headers = new HashMap<>();
        headers.put(EventHelper.EVENT_TYPE, domainEvent.getClass().getSimpleName());
        return new OutboxKafkaMessage(outboxEvent, headers);
    }
}
