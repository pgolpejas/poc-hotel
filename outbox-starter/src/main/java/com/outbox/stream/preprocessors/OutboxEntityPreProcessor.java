package com.outbox.stream.preprocessors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outbox.configuration.OutboxConfiguration;
import com.outbox.configuration.OutboxRepositoryFactory;
import com.outbox.data.*;
import com.outbox.data.mapper.OutboxEntityMapper;
import com.outbox.data.mapper.OutboxEntityMapperNotRegisteredException;
import com.outbox.data.mapper.OutboxEntityMapperResolver;
import com.outbox.events.OutboxEntityCreatedInternalEvent;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.cloud.stream.config.BindingProperties;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Slf4j
public abstract class OutboxEntityPreProcessor<E extends BaseEventEntity> {

    protected OutboxEntityMapperResolver outboxEntityMapperResolver;

    protected OutboxRepositoryFactory outboxRepositoryFactory;

    protected ApplicationEventPublisher applicationEventPublisher;

    protected OutboxConfiguration outboxConfiguration;

    protected KafkaAvroSerializer serializer;

    protected ObjectMapper mapper;
    
    protected BindingServiceProperties bindingServiceProperties;

    public abstract E create(Object domainEvent, String aggregateId);

    public boolean preProcess(final Object aggregateRoot, OutboxContext context)
            throws OutboxRuntimeException {
        StopWatch watch = StopWatch.create();
        watch.start();
        boolean result = internalPreProcess(aggregateRoot, context);
        watch.stop();
        log.debug("{} mils to pre process entity", watch.getTime());
        return result;
    }

    public abstract boolean internalPreProcess(Object aggregateRoot, OutboxContext context)
            throws OutboxRuntimeException;

    @SuppressWarnings({"rawtypes"})
    protected List<BaseEventEntity> generatesAndProcessEventEntity(final Object aggregateRoot, Object event,
                                                                   OutboxContext context) throws OutboxRuntimeException {

        final List<BaseEventEntity> events = new ArrayList<>();

        List<OutboxEntityMapper> outboxEntityMappers = this.outboxEntityMapperResolver.resolve(event.getClass());
        if (outboxEntityMappers == null || outboxEntityMappers.isEmpty()) {
            throw new OutboxEntityMapperNotRegisteredException(event.getClass());
        }
        String idempotenceIdentifier = UUID.randomUUID().toString();
        outboxEntityMappers.forEach(
                outboxEntityMapper -> generateForEachMapper(aggregateRoot, event, context, events, idempotenceIdentifier,
                        outboxEntityMapper));

        if (outboxConfiguration.isSendEventsJustInTime()) {
            // Must order these events.
            OutboxEntityCreatedInternalEvent internalEvent = new OutboxEntityCreatedInternalEvent(events);
            log.info("Send internal event:{}", internalEvent);
            this.applicationEventPublisher.publishEvent(internalEvent);
        } else {
            log.info("Events for aggregatedId:{} will be send only by scheduler", context.getAggregateId());
        }

        return events;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void generateForEachMapper(final Object aggregateRoot, Object event, OutboxContext context,
                                       final List<BaseEventEntity> events, String idempotenceIdentifier, OutboxEntityMapper outboxEntityMapper) {
        OutboxKafkaMessage messageWithHeaders = outboxEntityMapper.map(event);
        SpecificRecord messagePayload = messageWithHeaders.envelope();

        BaseEventEntity outboxEntity = create(event, context.getAggregateId());

        outboxEntity.setOutboxEventType(messagePayload.getClass().getName());
        String version = messagePayload.getSchema() != null && StringUtils.hasText(messagePayload.getSchema().getFullName())
                ? messagePayload.getSchema().getFullName()
                : "unknown";
        outboxEntity.setVersion(version);
        
        outboxEntity.setSendStrategyType(outboxConfiguration.getMode());

        Map<String, Object> specificHeaders = messageWithHeaders.specificHeaders();
        specificHeaders.put("outbox_event_source_id", idempotenceIdentifier);
        specificHeaders.put(KafkaHeaders.KEY, context.getAggregateId());

        String headersString;
        try {
            headersString = this.mapper.writeValueAsString(specificHeaders);
            outboxEntity.setHeadersMap(headersString);
        } catch (OutboxRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new OutboxRuntimeException("Error while processing headers:" + messagePayload, e);
        }

        byte[] toSend;
        try {
            toSend = serializer.serialize("", messagePayload);
        } catch (Exception me) {
            throw new OutboxRuntimeException("Error while serializing:" + messagePayload, me);
        }
        outboxEntity.setSerializedEvent(toSend);

        outboxEntity.setJsonSource(messagePayload.toString());

        outboxEntity.setAggregateRoot(aggregateRoot.getClass().getName());
        
        final String bindingName = this.outboxConfiguration.getBindingName(messagePayload.getClass().getName());
        outboxEntity.setDestinationTopic(this.getDestination(bindingName));

        OutboxRepository snapshotRepository = outboxRepositoryFactory.resolve(outboxEntity.getClass());

        log.info("Save event:{}", outboxEntity);
        snapshotRepository.save(outboxEntity);

        events.add(outboxEntity);
    }

    private String getDestination(final String bindingName) {
        final BindingProperties bindingProperties = this.bindingServiceProperties.getBindingProperties(bindingName);
        return bindingProperties.getDestination();
    }
}
