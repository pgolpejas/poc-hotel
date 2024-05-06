package com.outbox.events;

import com.outbox.data.BaseEventEntity;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Value
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OutboxEntityCreatedInternalEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 9009025295110985446L;

    private List<BaseEventEntity> entities;

}
