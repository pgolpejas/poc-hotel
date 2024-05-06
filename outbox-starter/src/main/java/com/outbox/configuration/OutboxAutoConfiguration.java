package com.outbox.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outbox.aop.OutboxAspect;
import com.outbox.data.OutboxRepository;
import com.outbox.data.mapper.OutboxEntityMapper;
import com.outbox.data.mapper.OutboxEntityMapperResolver;
import com.outbox.engine.OutboxCreatedListener;
import com.outbox.scheduler.OutboxScheduledService;
import com.outbox.scheduler.ParallelizableService;
import com.outbox.scheduler.ParallelizableServiceImpl;
import com.outbox.stream.OutboxPublisherTask;
import com.outbox.stream.postprocessors.OutboxEntityPostProcessor;
import com.outbox.stream.postprocessors.OutboxEntityPostProcessorImpl;
import com.outbox.stream.preprocessors.OutboxDomainEntityPreProcessorImpl;
import com.outbox.stream.preprocessors.OutboxEntityPreProcessor;
import com.outbox.stream.preprocessors.OutboxSnapshotEntityPreProcessorImpl;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@AutoConfiguration
@PropertySource({"classpath:outbox.properties"})
public class OutboxAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "outbox")
    public OutboxConfiguration outboxPatternConfiguration() {
        return new OutboxConfiguration();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    public OutboxEntityMapperResolver outboxEntityMapperResolver(
            final List<OutboxEntityMapper<?>> outboxEntityMappers) {

        Comparator<OutboxEntityMapper> byOrder = Comparator.comparing(OutboxEntityMapper::order);

        List<OutboxEntityMapper<?>> orderedSet = new ArrayList(outboxEntityMappers);
        orderedSet.sort(byOrder);
        return new OutboxEntityMapperResolver(orderedSet);
    }

    @Bean
    public OutboxPublisherTask transactionalOutboxPublisherTask(
            final OutboxEntityPostProcessor outboxEntityPostProcessor,
            @Lazy final OutboxRepositoryFactory outboxRepositoryFactory,
            final OutboxConfiguration outboxPatternConfiguration,
            final ParallelizableService parallelizableService) {
        return new OutboxPublisherTask(outboxRepositoryFactory, outboxPatternConfiguration,
                outboxEntityPostProcessor, parallelizableService);
    }

    @Bean
    @ConditionalOnProperty(value = "outbox.enableSchedule", havingValue = "true", matchIfMissing = true)
    public OutboxScheduledService outboxScheduledService(
            final OutboxPublisherTask transactionalOutboxPublisherTask) {
        return new OutboxScheduledService(transactionalOutboxPublisherTask);
    }

    @SuppressWarnings("rawtypes")
    @Bean
    public OutboxAspect transactionalOutboxAspect(Set<OutboxEntityPreProcessor> preProcessors) {
        return new OutboxAspect(preProcessors);
    }

    @Bean
    @ConditionalOnProperty(value = "outbox.enableOutboxSend", havingValue = "true",
            matchIfMissing = true)
    public OutboxDomainEntityPreProcessorImpl transactionalOutboxEntityPreProcessorImpl(
            final OutboxEntityMapperResolver outboxEntityMapperResolver,
            final OutboxRepositoryFactory outboxRepositoryFactory,
            final ApplicationEventPublisher applicationEventPublisher,
            final OutboxConfiguration outboxConfiguration,
            @Autowired(required = false) final KafkaAvroSerializer serializer,
            final ObjectMapper mapper,
            final BindingServiceProperties bindingServiceProperties) {
        return new OutboxDomainEntityPreProcessorImpl(outboxEntityMapperResolver, outboxRepositoryFactory,
                applicationEventPublisher, outboxConfiguration, serializer, mapper, bindingServiceProperties);
    }

    @Bean
    @ConditionalOnProperty(value = "outbox.enableSnapshotSend", havingValue = "true",
            matchIfMissing = true)
    public OutboxSnapshotEntityPreProcessorImpl transactionalSnapshotEntityPreProcessorImpl(
            final OutboxEntityMapperResolver outboxEntityMapperResolver,
            final OutboxRepositoryFactory outboxRepositoryFactory,
            final ApplicationEventPublisher applicationEventPublisher,
            final OutboxConfiguration outboxPatternConfiguration,
            @Autowired(required = false) final KafkaAvroSerializer serializer,
            final ObjectMapper mapper, final BindingServiceProperties bindingServiceProperties) {
        return new OutboxSnapshotEntityPreProcessorImpl(outboxEntityMapperResolver, outboxRepositoryFactory,
                applicationEventPublisher, outboxPatternConfiguration, serializer, mapper, bindingServiceProperties);
    }


    @SuppressWarnings("rawtypes")
    @Bean
    @Lazy
    public OutboxRepositoryFactory outboxRepositoryFactory(final Set<OutboxRepository> outboxRepository) {
        return new OutboxRepositoryFactory(outboxRepository);
    }

    @Bean
    public ParallelizableService parallelizableServiceImpl(final OutboxConfiguration outboxConfiguration) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        return new ParallelizableServiceImpl(outboxConfiguration, executorService);
    }

    @Bean
    public OutboxCreatedListener outboxCreatedListener(
            final OutboxEntityPostProcessor outboxEntityPostProcessor) {
        return new OutboxCreatedListener(outboxEntityPostProcessor);
    }

    @Bean
    public OutboxEntityPostProcessor outboxSnapshotProcessorImpl(OutboxRepositoryFactory outboxRepositoryFactory, ObjectMapper mapper,
                                                                 OutboxConfiguration config,
                                                                 @Autowired(required = false) final KafkaAvroDeserializer deserializer,
                                                                 StreamBridge streamBridge) {
        return new OutboxEntityPostProcessorImpl(outboxRepositoryFactory, config, mapper, deserializer, streamBridge);
    }

}
