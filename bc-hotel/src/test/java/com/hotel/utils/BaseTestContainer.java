package com.hotel.utils;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"outbox.enableSnapshotSend=false", "outbox.enableOutboxSend=false", "outbox.enableSchedule=false"})
@SqlMergeMode(MergeMode.MERGE)
@Sql(scripts = "/sql/truncate.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
public abstract class BaseTestContainer {

    private static final String POSTGRES_USERNAME = "admin";
    private static final String POSTGRES_PASSWORD = "admin";
    private static final String POSTGRES_DATABASE = "hotel-db";

    static PostgreSQLContainer<?> postgresDBContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.2"))
            .withExposedPorts(5432)
            .withDatabaseName(POSTGRES_DATABASE)
            .withUsername(POSTGRES_USERNAME)
            .withPassword(POSTGRES_PASSWORD);

    static {
        postgresDBContainer.start();
    }

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        // postgresql
        registry.add("spring.datasource.url", postgresDBContainer::getJdbcUrl);
        registry.add("spring.datasource.jdbcUrl", postgresDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresDBContainer::getUsername);
        registry.add("spring.datasource.password", postgresDBContainer::getPassword);
        registry.add("spring.datasource.driverClassName", postgresDBContainer::getDriverClassName);
    }

}
