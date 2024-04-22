package com.hotel.core.configuration;

import java.util.Map;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class is mandatory in order to integrate outbox pattern library and the configuration of Avro serializers/deserializers
 */
@Configuration
public class ConfluentSchemaRegistryClientConfiguration {

  @Bean
  public SerializerProperties serializerProperties() {
    return new SerializerProperties();
  }

  @Bean
  public DeserializerProperties deserializerProperties() {
    return new DeserializerProperties();
  }

  @Bean
  public SchemaRegistryClient confluentSchemaRegistryClient(
      @Value("${spring.cloud.stream.schema-registry-client.endpoint:http://localhost:8081}") String endpoint) {
    return new CachedSchemaRegistryClient(endpoint, 10000, Map.of("http.connect.timeout.ms", "1000", "http.read.timeout.ms", "1000"));
  }

  @Bean
  public Serializer<Object> confluentAvroSerializer(
      @Qualifier("confluentSchemaRegistryClient") final SchemaRegistryClient confluentSchemaRegistryClient,
      SerializerProperties serializerProperties) {
    return new KafkaAvroSerializer(confluentSchemaRegistryClient,
        Map.of("schema.registry.url", serializerProperties.getSchemaRegistryUrl(),
            "value.subject.name.strategy", serializerProperties.getValueSubjectNameStrategy(),
            "auto.register.schemas", serializerProperties.isAutoRegisterSchemas(),
            "use.latest.version", serializerProperties.isUseLatestVersion(),
            "schema.reflection", serializerProperties.isSchemaReflection())
    );
  }

  @Bean
  public Deserializer<Object> confluentAvroDeserializer(
      @Qualifier("confluentSchemaRegistryClient") final SchemaRegistryClient confluentSchemaRegistryClient,
      DeserializerProperties deserializerProperties) {
    return new KafkaAvroDeserializer(confluentSchemaRegistryClient,
        Map.of("schema.registry.url", deserializerProperties.getSchemaRegistryUrl(),
            "value.subject.name.strategy", deserializerProperties.getValueSubjectNameStrategy(),
            "use.latest.version", deserializerProperties.isUseLatestVersion(),
            "schema.reflection", deserializerProperties.isSchemaReflection(),
            "specific.avro.reader", deserializerProperties.isSpecificAvroReader()));
  }
}
