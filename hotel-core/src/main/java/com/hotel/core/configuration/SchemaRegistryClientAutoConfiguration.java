package com.hotel.core.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.schema.registry.client.ConfluentSchemaRegistryClient;
import org.springframework.cloud.stream.schema.registry.client.SchemaRegistryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class is mandatory to change spring schema registry server by confluent schema registry client
 */
@Configuration
public class SchemaRegistryClientAutoConfiguration {

    @Bean
    public SchemaRegistryClient schemaRegistryClient(@Value("${spring.cloud.stream.schema-registry-client.endpoint:http://localhost:8081}") String endpoint) {
        ConfluentSchemaRegistryClient client = new ConfluentSchemaRegistryClient();
        client.setEndpoint(endpoint);
        return client;
    }


}
