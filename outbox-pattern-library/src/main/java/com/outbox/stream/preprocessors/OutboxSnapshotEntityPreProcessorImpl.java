package com.outbox.stream.preprocessors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outbox.configuration.OutboxConfiguration;
import com.outbox.configuration.OutboxRepositoryFactory;
import com.outbox.data.BaseEventEntity;
import com.outbox.data.OutboxContext;
import com.outbox.data.OutboxRuntimeException;
import com.outbox.data.SnapshotEntity;
import com.outbox.data.mapper.OutboxEntityMapperResolver;
import com.outbox.data.mapper.OutboxEntityMapperSerializingException;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

public class OutboxSnapshotEntityPreProcessorImpl extends OutboxEntityPreProcessor<SnapshotEntity> {

    public OutboxSnapshotEntityPreProcessorImpl(OutboxEntityMapperResolver outboxEntityMapperResolver,
                                                OutboxRepositoryFactory outboxRepositoryFactory,
                                                ApplicationEventPublisher applicationEventPublisher,
                                                OutboxConfiguration outboxPatternConfiguration,
                                                final KafkaAvroSerializer serializer,
                                                final ObjectMapper mapper,
                                                final BindingServiceProperties bindingServiceProperties) {
        super(outboxEntityMapperResolver, outboxRepositoryFactory, applicationEventPublisher,
                outboxPatternConfiguration, serializer, mapper, bindingServiceProperties);
    }


    public SnapshotEntity create(Object domainEvent, String aggregateId) throws OutboxRuntimeException {
        try {
            return SnapshotEntity.create(domainEvent.getClass().getName(), aggregateId,
                    outboxConfiguration.getMode(), domainEvent);
        } catch (Exception mex) {
            throw new OutboxEntityMapperSerializingException("Serializing error:" + mex.getMessage());
        }
    }

    public boolean internalPreProcess(final Object aggregateRoot, OutboxContext context)
            throws AggregateEventsNotFound, OutboxRuntimeException {

        List<BaseEventEntity> snapshotEntity = generatesAndProcessEventEntity(aggregateRoot, aggregateRoot, context);

        return snapshotEntity != null && !snapshotEntity.isEmpty();
    }

}
