package com.reservation.infrastructure.producer.mapper;

import com.hotel.core.domain.ddd.AggregateRoot;
import com.hotel.core.domain.ddd.DomainEvent;
import com.hotel.core.domain.utils.EventHelper;
import com.outbox.data.OutboxKafkaMessage;
import com.outbox.data.mapper.OutboxEntityMapper;
import com.roomTypeInventory.domain.avro.v1.RoomTypeInventorySnapshot;
import com.reservation.domain.model.RoomTypeInventory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomTypeInventorySnapshotOutboxMapper implements OutboxEntityMapper<RoomTypeInventory> {

  private final RoomTypeInventoryEventMapper eventMapper;

  @Override
  public OutboxKafkaMessage map(final RoomTypeInventory dataEvent) {
    final RoomTypeInventorySnapshot outboxEvent = this.eventMapper.mapRoomTypeInventory(dataEvent);
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
