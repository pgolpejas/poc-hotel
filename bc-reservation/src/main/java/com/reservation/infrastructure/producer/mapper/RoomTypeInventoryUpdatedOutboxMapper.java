package com.reservation.infrastructure.producer.mapper;

import com.hotel.core.domain.utils.EventHelper;
import com.outbox.data.OutboxKafkaMessage;
import com.outbox.data.mapper.OutboxEntityMapper;
import com.roomTypeInventory.domain.avro.v1.RoomTypeInventoryUpdated;
import com.reservation.domain.event.RoomTypeInventoryUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomTypeInventoryUpdatedOutboxMapper implements OutboxEntityMapper<RoomTypeInventoryUpdatedEvent> {

    private final RoomTypeInventoryEventMapper eventMapper;

    @Override
    public OutboxKafkaMessage map(final RoomTypeInventoryUpdatedEvent domainEvent) {
        final RoomTypeInventoryUpdated outboxEvent = this.eventMapper.mapRoomTypeInventoryUpdated(domainEvent);

        final Map<String, Object> headers = new HashMap<>();
        headers.put(EventHelper.EVENT_TYPE, domainEvent.getClass().getSimpleName());
        return new OutboxKafkaMessage(outboxEvent, headers);
    }
}
