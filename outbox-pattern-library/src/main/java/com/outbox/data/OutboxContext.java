package com.outbox.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OutboxContext {

    private String pullEventsFrom;

    private String aggregateId;

}
