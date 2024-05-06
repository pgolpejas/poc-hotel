package com.hotel.infrastructure.producer.mapper;

import com.hotel.core.domain.ddd.AggregateRoot;
import com.hotel.core.domain.ddd.DomainEvent;
import com.hotel.core.domain.utils.EventHelper;
import com.hotel.domain.model.Room;
import com.outbox.data.OutboxKafkaMessage;
import com.outbox.data.mapper.OutboxEntityMapper;
import com.room.domain.avro.v1.RoomSnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomSnapshotOutboxMapper implements OutboxEntityMapper<Room> {

  private final RoomEventMapper eventMapper;

  @Override
  public OutboxKafkaMessage map(final Room dataEvent) {
    final RoomSnapshot outboxEvent = this.eventMapper.mapRoom(dataEvent);
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
