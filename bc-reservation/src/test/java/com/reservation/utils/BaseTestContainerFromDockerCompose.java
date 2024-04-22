package com.reservation.utils;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.shaded.org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
@SqlMergeMode(MergeMode.MERGE)
@Sql(scripts = "/sql/truncate.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
public abstract class BaseTestContainerFromDockerCompose {

    public static final String POSTGRES_SERVICE_NAME = "reservation-db-it";

    public static final String ZOOKEEPER_SERVICE_NAME = "zookeeper-it";

    public static final String KAFKA_SERVICE_NAME = "kafka-broker-1-it";

    public static final String SCHEMA_REGISTRY_SERVICE_NAME = "schema-registry-it";

    @Value(value = "${local.server.port}")
    protected int port;


    private static final DockerComposeContainer<?> compose = new DockerComposeContainer<>(
            new File(ClassLoader.getSystemResource("compose/docker-compose.yml").getPath()));

    static {
        final Boolean localContainers = Boolean.parseBoolean(System.getenv("LOCAL_CONTAINERS"));

        if (!localContainers) {
            startCompose();
        }
    }

    private static void startCompose() {

        final Map<String, Integer> servicePort = prepareCompose();

        printLogs();

        compose.waitingFor(POSTGRES_SERVICE_NAME, Wait.forHealthcheck().withStartupTimeout(Duration.ofMinutes(2)));
        compose.waitingFor(KAFKA_SERVICE_NAME, Wait.forHealthcheck().withStartupTimeout(Duration.ofMinutes(5)));
        compose.waitingFor(SCHEMA_REGISTRY_SERVICE_NAME, Wait.forHealthcheck().withStartupTimeout(Duration.ofMinutes(5)));
        compose.waitingFor(ZOOKEEPER_SERVICE_NAME, Wait.forHealthcheck().withStartupTimeout(Duration.ofMinutes(5)));

        compose.start();

        configFramework(servicePort);
    }

    private static void printLogs() {

        final Slf4jLogConsumer consumer = new Slf4jLogConsumer(LoggerFactory.getLogger(BaseTestContainerFromDockerCompose.class));

        compose.withLogConsumer(POSTGRES_SERVICE_NAME, consumer);
        compose.withLogConsumer(ZOOKEEPER_SERVICE_NAME, consumer);
        compose.withLogConsumer(KAFKA_SERVICE_NAME, consumer);
        compose.withLogConsumer(SCHEMA_REGISTRY_SERVICE_NAME, consumer);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Integer> prepareCompose() {

        final Map<String, Object> composeContent = new Yaml().load(ClassLoader.getSystemResourceAsStream("compose/docker-compose.yml"));
        final Map<String, Object> composeServices = (Map<String, Object>) composeContent.get("services");

        final Map<String, Integer> servicePort = new HashMap<>();

        for (final Entry<String, Object> composeServicesEntry : composeServices.entrySet()) {
            final String composeServiceName = composeServicesEntry.getKey();

            final Map<String, Object> composeService = (Map<String, Object>) composeServicesEntry.getValue();

            if (null != composeService.get("ports")) {
                final List<String> composeServicePorts = (List<String>) composeService.get("ports");
                for (final String composeServicePort : composeServicePorts) {
                    final String[] ports = composeServicePort.split(":");
                    compose.withExposedService(composeServiceName, Integer.parseInt(ports[1]));
                    servicePort.put(composeServiceName, Integer.parseInt(ports[1]));
                }
            }


        }
        return servicePort;
    }

    private static void configFramework(final Map<String, Integer> servicePort) {

        servicePort.forEach((service, port) -> {
            System.setProperty("docker." + service + ".host", compose.getServiceHost(service, port));
            System.setProperty("docker." + service + ".port", String.valueOf(compose.getServicePort(service, port)));
        });
    }

}
