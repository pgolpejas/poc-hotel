package com.outbox.stream.preprocessors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outbox.configuration.OutboxConfiguration;
import com.outbox.configuration.OutboxRepositoryFactory;
import com.outbox.data.BaseEventEntity;
import com.outbox.data.OutboxContext;
import com.outbox.data.OutboxEntity;
import com.outbox.data.OutboxRuntimeException;
import com.outbox.data.mapper.OutboxEntityMapperResolver;
import com.outbox.data.mapper.OutboxEntityMapperSerializingException;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.ApplicationEventPublisher;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

@Slf4j
public class OutboxDomainEntityPreProcessorImpl extends OutboxEntityPreProcessor<OutboxEntity> {

    public OutboxDomainEntityPreProcessorImpl(OutboxEntityMapperResolver outboxEntityMapperResolver,
                                              OutboxRepositoryFactory outboxRepositoryFactory,
                                              ApplicationEventPublisher applicationEventPublisher,
                                              OutboxConfiguration outboxConfiguration,
                                              final KafkaAvroSerializer serializer,
                                              final ObjectMapper mapper,
                                              final BindingServiceProperties bindingServiceProperties) {
        super(outboxEntityMapperResolver, outboxRepositoryFactory, applicationEventPublisher,
                outboxConfiguration, serializer,mapper, bindingServiceProperties);
    }

    public OutboxEntity create(Object domainEvent, String aggregateId) {
        try {
            return OutboxEntity.create(domainEvent.getClass().getName(), aggregateId,
                    outboxConfiguration.getMode(), domainEvent, null, null);

        } catch (Exception mex) {
            throw new OutboxEntityMapperSerializingException("Serializing error:" + mex.getMessage());
        }
    }

    public boolean internalPreProcess(final Object aggregateRoot, OutboxContext context)
            throws AggregateEventsNotFound, OutboxRuntimeException {

        Collection<Serializable> domainEvents;
        try {
            domainEvents = this.extractDomainEventsFrom(aggregateRoot, context.getPullEventsFrom());
        } catch (Exception nsme) {
            throw new AggregateEventsNotFound("There was an error extracting events from aggregate:" + aggregateRoot,
                    nsme);
        }
        if (domainEvents != null) {
            domainEvents.forEach(domainEvent -> {

                List<BaseEventEntity> outboxEntities = generatesAndProcessEventEntity(aggregateRoot, domainEvent,
                        context);
                log.info("Entities {} processed", outboxEntities.size());
            });
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<Serializable> extractDomainEventsFrom(final Object aggregateRoot, final String methodName)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Method method = aggregateRoot.getClass().getMethod(methodName);
        return (Collection<Serializable>) method.invoke(aggregateRoot);
    }

}
