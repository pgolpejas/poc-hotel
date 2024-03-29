package com.pocspringbootkafka.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ObjectMapperProducerFactoryCustomizer implements DefaultKafkaProducerFactoryCustomizer {

    private final ObjectMapper objectMapper;

    public ObjectMapperProducerFactoryCustomizer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void customize(DefaultKafkaProducerFactory<?, ?> producerFactory) {
        if (Objects.nonNull(producerFactory)) {
            producerFactory.setValueSerializer(new JsonSerializer<>(objectMapper));
        }
    }

}