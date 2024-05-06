package com.outbox.configuration;

import com.outbox.data.OutboxRepository;
import com.outbox.stream.OutboxRepositoryNotRegistered;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
@Getter
public class OutboxRepositoryFactory {

    @SuppressWarnings("rawtypes")
    private final Set<OutboxRepository> outboxRepositories;


    @SuppressWarnings({"rawtypes", "unchecked"})
    public OutboxRepository resolve(Class<?> eventClass) {
        return outboxRepositories.stream()
                .filter(publisher -> publisher.canStore(eventClass))
                .findFirst()
                .orElseThrow(() -> new OutboxRepositoryNotRegistered(eventClass));

    }

}
