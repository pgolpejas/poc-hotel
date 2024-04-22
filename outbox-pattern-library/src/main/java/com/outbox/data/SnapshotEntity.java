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
public class SnapshotEntity extends BaseEventEntity {

    public static SnapshotEntity create(String aggregateType, String aggregateId, String strategy, Object sourceEvent) {

        SnapshotEntity entity = new SnapshotEntity();
        entity.setId(UUID.randomUUID());
        entity.setCreatedAt(Instant.now());
        entity.setAggregateType(aggregateType);
        entity.setAggregateId(aggregateId);
        entity.setDomainEvent(sourceEvent);
        entity.setSendStrategyType(strategy);
        return entity;

    }

}
