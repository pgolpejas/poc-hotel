# README #

## Metrics ##

### BC-Hotel ###
![coverage](.github/badges/jacocoBCHotel.svg)
![branches](.github/badges/branchesBCHotel.svg)

### BC-Reservation ###
![coverage](.github/badges/jacocoBCReservation.svg)
![branches](.github/badges/branchesBCReservation.svg)

### Sonar ###
[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-black.svg)](https://sonarcloud.io/dashboard?id=poc-hotel)

[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=poc-hotel&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=poc-hotel)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=poc-hotel&metric=coverage)](https://sonarcloud.io/dashboard?id=poc-hotel)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=poc-hotel&metric=ncloc)](https://sonarcloud.io/dashboard?id=poc-hotel)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=poc-hotel&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=poc-hotel)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=poc-hotel&metric=bugs)](https://sonarcloud.io/dashboard?id=poc-hotel)

## What is this service for? ##

* POC with:
  * Java 21
  * Spring boot 3
  * PostgreSQL 16
  * Api first
  * Kafka 
  * Hexagonal Architecture
  * DDD
  * Audit with javers
  * TestContainers
  * K6

## How to build and run this service ##

### Pre-requisites ###

In order to build this service the following software should be installed and added to your PATH:

- Java version 21
- Apache Maven 3 (3.9.6+ recommended) (<https://maven.apache.org/download.cgi>)
- Docker

### Building and running ###

First, run docker-compose with postgresql, kafka and observability configuration
```
cd docker 
docker-compose up -d
```
Once started, the kafka-ui (akhq) can be reached at <http://localhost:9000>.

First, compile and generate the jar artifact.
```
mvn clean install
```

### BC-Hotel ###

At this point you could run locally your service with:

TODO add opentelemetry configuration on each DockerFile configuration

Once started, the service can be reached at <http://localhost:8080>.

### BC-Reservation ###

At this point you could run locally your service with:

TODO add opentelemetry configuration on each DockerFile configuration

Once started, the service can be reached at <http://localhost:8081>.

### Hotel-core ###

Library with core utilities

### Outbox-starter ###

Library with outbox-pattern implementation

## Coverage ## 

Run coverage with maven (docker-compose does not need to be running)
```
mvn clean verify
```

Run sonar
```
mvn sonar:sonar -Dsonar.login={{SONAR_TOKEN}}
```

Open target/site/jacoco-ut/index.html on every module and check coverage

## Observability ## 

We use the grafana stack to monitor the application: loki, tempo, grafana.

We deploy the stack using docker compose.

Open grafana in http://localhost:3000. You can view logs with Loki, metrics and traces with tempo

## Liquibase ## 

Generate changeLog from current database from parent project. Change the configuration of objective database in pom.xml

```
<plugin>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-maven-plugin</artifactId>
    <version>${liquibase.version}</version>
    <configuration>
        <driver>org.postgresql.Driver</driver>
        <url>jdbc:postgresql://localhost:5533/reservation-db</url>
        <username>admin</username>
        <password>admin</password>
        <outputChangeLogFile>bc-reservation/src/main/resources/liquibase-outputChangeLog.xml</outputChangeLogFile>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>${postgres.version}</version>
        </dependency>
    </dependencies>
</plugin>
```

Run

```
mvn liquibase:generateChangeLog
```

NOTE: It doesn't work from the bc-reservation project because it can't find the driver.

## Performance test with k6 ## 

TODO
