package com.outbox.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class OutboxEntity extends BaseEventEntity {


    public static OutboxEntity create(String aggregateType, String aggregateId, String strategy, Object sourceEvent,
                                      byte[] serial, String jsonSource) {

        OutboxEntity entity = new OutboxEntity();
        entity.setId(UUID.randomUUID());
        entity.setCreatedAt(Instant.now());
        entity.setAggregateType(aggregateType);
        entity.setAggregateId(aggregateId);
        entity.setDomainEvent(sourceEvent);
        entity.setSendStrategyType(strategy);

        entity.setJsonSource(jsonSource);

        entity.setSerializedEvent(serial);
        return entity;

    }

    public static OutboxEntity create(String aggregateRootType, String aggregateType, String aggregateId,
                                      String strategy, Object sourceEvent,
                                      byte[] serial, String jsonSource) {

        OutboxEntity entity = new OutboxEntity();
        entity.setId(UUID.randomUUID());
        entity.setCreatedAt(Instant.now());
        entity.setAggregateRoot(aggregateRootType);
        entity.setAggregateType(aggregateType);
        entity.setAggregateId(aggregateId);
        entity.setDomainEvent(sourceEvent);
        entity.setSendStrategyType(strategy);

        entity.setJsonSource(jsonSource);

        entity.setSerializedEvent(serial);
        return entity;

    }


}
