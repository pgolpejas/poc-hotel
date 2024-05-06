package com.hotel.infrastructure.producer.mapper;

import com.hotel.core.domain.utils.EventHelper;
import com.hotel.domain.event.RoomDomainEvent.RoomDeletedEvent;
import com.outbox.data.OutboxKafkaMessage;
import com.outbox.data.mapper.OutboxEntityMapper;
import com.room.domain.avro.v1.RoomDeleted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomDeletedOutboxMapper implements OutboxEntityMapper<RoomDeletedEvent> {

    private final RoomEventMapper eventMapper;

    @Override
    public OutboxKafkaMessage map(final RoomDeletedEvent domainEvent) {
        final RoomDeleted outboxEvent = this.eventMapper.mapRoomDeleted(domainEvent);

        final Map<String, Object> headers = new HashMap<>();
        headers.put(EventHelper.EVENT_TYPE, domainEvent.getClass().getSimpleName());
        return new OutboxKafkaMessage(outboxEvent, headers);
    }
}
