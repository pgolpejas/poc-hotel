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
  * Kafka 
  * Hexagonal Architecture
  * DDD
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
Once started, the kafka-ui can be reached at <http://localhost:8100>.

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

## Coverage ## 

Run coverage with maven (docker-compose does not need to be running)
```
mvn clean verify
```

Open target/site/jacoco-ut/index.html on every module and check coverage

## Observability ## 

Open grafana in http://localhost:3000. You can view logs with Loki, metrics and traces with tempo

Loki query

TODO

## Performance test with k6 ## 

TODO
