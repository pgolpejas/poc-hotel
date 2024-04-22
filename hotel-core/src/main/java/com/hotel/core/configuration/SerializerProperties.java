package com.hotel.core.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class SerializerProperties {

  @Value("${spring.cloud.stream.kafka.binder.producer-properties.schema.registry.url}")
  private String schemaRegistryUrl;

  @Value("${spring.cloud.stream.kafka.binder.producer-properties.value.subject.name.strategy}")
  private String valueSubjectNameStrategy;

  @Value("${spring.cloud.stream.kafka.binder.producer-properties.auto.register.schemas}")
  private boolean autoRegisterSchemas;

  @Value("${spring.cloud.stream.kafka.binder.producer-properties.use.latest.version}")
  private boolean useLatestVersion;

  @Value("${spring.cloud.stream.kafka.binder.producer-properties.schema.reflection}")
  private boolean schemaReflection;

}
