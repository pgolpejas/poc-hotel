package com.reservation.infrastructure.producer.mapper;

import java.util.HashMap;
import java.util.Map;

import com.hotel.core.domain.utils.EventHelper;
import com.outbox.data.OutboxKafkaMessage;
import com.outbox.data.mapper.OutboxEntityMapper;
import com.reservation.domain.event.RoomTypeInventoryDomainEvent.RoomTypeInventoryDeletedEvent;
import com.roomTypeInventory.domain.avro.v1.RoomTypeInventoryDeleted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomTypeInventoryDeletedOutboxMapper implements OutboxEntityMapper<RoomTypeInventoryDeletedEvent> {

  private final RoomTypeInventoryEventMapper eventMapper;

  @Override
  public OutboxKafkaMessage map(final RoomTypeInventoryDeletedEvent domainEvent) {
    final RoomTypeInventoryDeleted outboxEvent = this.eventMapper.mapRoomTypeInventoryDeleted(domainEvent);

    final Map<String, Object> headers = new HashMap<>();
    headers.put(EventHelper.EVENT_TYPE, domainEvent.getClass().getSimpleName());
    return new OutboxKafkaMessage(outboxEvent, headers);
  }
}
