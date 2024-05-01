package com.reservation.infrastructure.producer.mapper;

import java.util.HashMap;
import java.util.Map;

import com.hotel.core.domain.utils.EventHelper;
import com.outbox.data.OutboxKafkaMessage;
import com.outbox.data.mapper.OutboxEntityMapper;
import com.reservation.domain.event.RoomTypeInventoryDomainEvent.RoomTypeInventoryCreatedEvent;
import com.roomTypeInventory.domain.avro.v1.RoomTypeInventoryCreated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomTypeInventoryCreatedOutboxMapper implements OutboxEntityMapper<RoomTypeInventoryCreatedEvent> {

  private final RoomTypeInventoryEventMapper eventMapper;

  @Override
  public OutboxKafkaMessage map(final RoomTypeInventoryCreatedEvent domainEvent) {
    final RoomTypeInventoryCreated outboxEvent = this.eventMapper.mapRoomTypeInventoryCreated(domainEvent);

    final Map<String, Object> headers = new HashMap<>();
    headers.put(EventHelper.EVENT_TYPE, domainEvent.getClass().getSimpleName());
    return new OutboxKafkaMessage(outboxEvent, headers);
  }
}
