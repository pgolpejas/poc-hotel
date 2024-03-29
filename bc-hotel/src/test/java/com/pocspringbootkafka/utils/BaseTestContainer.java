package com.pocspringbootkafka.utils;

import com.pocspringbootkafka.hotelavailability.domain.model.HotelAvailabilitySearch;
import com.pocspringbootkafka.shared.constants.AppConstants;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

@Testcontainers
@Import(BaseTestContainer.KafkaTestContainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseTestContainer {

    private static final String POSTGRES_USERNAME = "admin";
    private static final String POSTGRES_PASSWORD = "admin";
    private static final String POSTGRES_DATABASE = "poc-db-postgresql";

    static PostgreSQLContainer<?> postgresDBContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.3"))
        .withExposedPorts(5432)
        .withDatabaseName(POSTGRES_DATABASE)
        .withUsername(POSTGRES_USERNAME)
        .withPassword(POSTGRES_PASSWORD);

    static KafkaContainer kafkaContainer =
        new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.0"))
            .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
            .withEnv("KAFKA_GROUP_MAX_SESSION_TIMEOUT_MS", "1000000")
            .withEnv("KAFKA_CREATE_TOPICS", AppConstants.TOPIC_NAME)
            //.waitingFor(Wait.forLogMessage("Created topic .*", 3).withStartupTimeout(Duration.ofSeconds(30)))
            .withReuse(true);

    static {
        postgresDBContainer.start();
        kafkaContainer.start();
    }

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        // postgresql
        registry.add("spring.datasource.url", postgresDBContainer::getJdbcUrl);
        registry.add("spring.datasource.jdbcUrl", postgresDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresDBContainer::getUsername);
        registry.add("spring.datasource.password", postgresDBContainer::getPassword);
        registry.add("spring.datasource.driverClassName", postgresDBContainer::getDriverClassName);

        //kafka
        registry.add("spring.kafka.producer.bootstrapServers", kafkaContainer::getBootstrapServers);
        registry.add("spring.kafka.consumer.bootstrapServers", kafkaContainer::getBootstrapServers);
        registry.add("spring.kafka.consumer.properties.auto.offset.reset", () -> "earliest");
    }

    @TestConfiguration
    static class KafkaTestContainersConfiguration {

        @Bean
        ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
            ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(consumerFactory());
            return factory;
        }

        @Bean
        public ConsumerFactory<String, String> consumerFactory() {
            return new DefaultKafkaConsumerFactory<>(consumerConfigs());
        }

        @Bean
        public Map<String, Object> consumerConfigs() {
            Map<String, Object> props = new HashMap<>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            props.put(ConsumerConfig.GROUP_ID_CONFIG, AppConstants.GROUP_ID);
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            return props;
        }

        @Bean
        public ProducerFactory<String, HotelAvailabilitySearch> producerFactory() {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            return new DefaultKafkaProducerFactory<>(configProps);
        }

        @Bean
        public KafkaTemplate<String, HotelAvailabilitySearch> kafkaTemplate() {
            return new KafkaTemplate<>(producerFactory());
        }
    }
}
