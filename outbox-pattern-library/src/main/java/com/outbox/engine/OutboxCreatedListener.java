package com.outbox.engine;

import com.outbox.events.OutboxEntityCreatedInternalEvent;
import com.outbox.stream.postprocessors.OutboxEntityPostProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@AllArgsConstructor
@Slf4j
public class OutboxCreatedListener {

    private final OutboxEntityPostProcessor outboxEntityPostProcessor;


    @TransactionalEventListener(classes = OutboxEntityCreatedInternalEvent.class,
            phase = TransactionPhase.AFTER_COMMIT)
    public void onSnapshotCreatedEvent(final OutboxEntityCreatedInternalEvent entity) {
        log.debug("onEntityCreatedEvent for:" + entity);
        this.outboxEntityPostProcessor.process(entity.getEntities(), true);
    }

}
