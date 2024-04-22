package com.hotel.core.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;


@Data
public class DeserializerProperties {

    @Value("${spring.cloud.stream.kafka.binder.consumer-properties.schema.registry.url}")
    private String schemaRegistryUrl;

    @Value("${spring.cloud.stream.kafka.binder.consumer-properties.value.subject.name.strategy}")
    private String valueSubjectNameStrategy;

    @Value("${spring.cloud.stream.kafka.binder.consumer-properties.use.latest.version}")
    private boolean useLatestVersion;

    @Value("${spring.cloud.stream.kafka.binder.consumer-properties.schema.reflection}")
    private boolean schemaReflection;
    
    @Value("${spring.cloud.stream.kafka.binder.consumer-properties.specific.avro.reader}")
    private boolean specificAvroReader;

}
