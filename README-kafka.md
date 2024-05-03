# Kakfa #

## Implementation ##

We use confluent platform in local with docker compose:
* Zookeeper
* Kafka broker
* Schema registry
* Schema registry import
* AKHQ (Kakfa UI)

We have two kind of events:
* Domain events: for actions inside BC
* Data events: snapshots events

## Asyncapi contract ##

In asyncapi.yml we define the events used in the application

## Configuration ##

In the application.yml we need to have this configuration:

```
spring:
  cloud:
      # To consume domain events we need to use the function router
    function:
      definition: functionRouter
      routing-expression: "'handle' + headers['event_type']"    
    stream:
      function:
        routing:
          enabled: true
      default:
        producer:
          useNativeEncoding: true
      #        consumer:
      #          useNativeEncoding: true
      kafka:
        binder:
          defaultBrokerPort: 19092
          
          # Disabled because schemas are registered from maven plugin
          auto-create-topics: false # Disabling this seem to override the server settings and will auto create
          auto-add-partitions: false # I wonder if its cause this is set
          
          producer-properties:
            schema.registry.url: http://localhost:8081
            
            #This will use a string based key - aka not in the registry - don't need a name strategy with string serializer
            key.serializer: org.apache.kafka.common.serialization.StringSerializer

            # This will control the Serializer Setup
            value.subject.name.strategy: io.confluent.kafka.serializers.subject.RecordNameStrategy
            value.serializer: io.confluent.kafka.serializers.KafkaAvroSerializer

            # https://www.confluent.io/es-es/blog/multiple-event-types-in-the-same-kafka-topic/ 
            # Disable for auto schema registration
            auto.register.schemas: false

            # Use only the latest schema version
            use.latest.version: true

            # This will use reflection to generate schemas from classes - used to validate current data set
            # against the schema registry for valid production
            schema.reflection: true
          
          consumer-properties:
            key.deserializer: org.apache.kafka.common.serialization.StringDeserializer
            value.deserializer: io.confluent.kafka.serializers.KafkaAvroDeserializer
            schema.registry.url: http://localhost:8081
            specific.avro.reader: true
            
            # This will control the Serializer Setup
            value.subject.name.strategy: io.confluent.kafka.serializers.subject.RecordNameStrategy
            
            # Use only the latest schema version
            use.latest.version: true

            # This will use reflection to generate schemas from classes - used to validate current data set
            # against the schema registry for valid production
            schema.reflection: true
      
      schema-registry-client:
        endpoint: http://localhost:8081
      
      # Disabled because schemas are registered from maven plugin
      #      schema:
      #        avro:
      #          schema-locations: classpath:asyncapi/v1/imports/*.avsc
      bindings:
        functionRouter-in-0:
          destination: poc.reservation.domain.v1,poc.reservation.data.v1,poc.roomTypeInventory.data.v1,poc.roomTypeInventory.domain.v1
          contentType: application/*+avro
          group: group-functionRouter-consumer
        reservation-data-out-0:
          destination: poc.reservation.data.v1
          contentType: application/*+avro
          group: group-reservation-data-producer
        reservation-domain-out-0:
          destination: poc.reservation.domain.v1
          contentType: application/*+avro
          group: group-reservation-domain-producer
        roomTypeInventory-data-out-0:
          destination: poc.roomTypeInventory.data.v1
          contentType: application/*+avro
          group: group-roomTypeInventory-data-producer
        roomTypeInventory-domain-out-0:
          destination: poc.roomTypeInventory.domain.v1
          contentType: application/*+avro
          group: group-roomTypeInventory-domain-producer
```

## Schema configuration ##

Although Spring Boot creates the schemas, subjects and topics by default, this approach is not valid for us. We need a topic that can consume different schemas for domain events

To do this, we create a script to import the necessary objects. This script is run inside a Docker container.

```
schema-registry-autoimport.sh
```

This script imports the schemas folder 'v1/imports' and creates a schema envelope that groups different types of events into the same schema.

This script simulates these requests to schema registry:

### Schema com.reservation.domain.avro.v1.ReservationCreated ###

```
curl --location 'http://localhost:8081/subjects/com.reservation.domain.avro.v1.ReservationCreated/versions' \
--header 'Content-Type: application/vnd.schemaregistry.v1+json' \
--data '{
"schema": "{\"type\":\"record\",\"name\":\"ReservationCreated\",\"namespace\":\"com.reservation.domain.avro.v1\",\"doc\":\"Reservation created domain event\",\"fields\":[{\"name\":\"id\",\"type\":{\"type\":\"string\",\"logicalType\":\"uuid\"},\"doc\":\"id, example: '\''1fa85f64-5717-4562-b3fc-2c963f66afa6'\''\"},{\"name\":\"version\",\"type\":\"int\"},{\"name\":\"roomTypeId\",\"type\":\"int\",\"doc\":\"roomTypeId, example: '\''1'\''\"},{\"name\":\"hotelId\",\"type\":{\"type\":\"string\",\"logicalType\":\"uuid\"},\"doc\":\"hotelId, example: '\''3fa85f64-5717-4562-b3fc-2c963f66afa6'\''\"},{\"name\":\"guestId\",\"type\":{\"type\":\"string\",\"logicalType\":\"uuid\"},\"doc\":\"guestId, example: '\''4fa85f64-5717-4562-b3fc-2c963f66afa6'\''\"},{\"name\":\"start\",\"type\":\"string\",\"doc\":\"start date, example: '\''2024-05-26'\''\"},{\"name\":\"end\",\"type\":\"string\",\"doc\":\"end date, example: '\''2024-05-26'\''\"},{\"name\":\"status\",\"type\":[\"null\",\"string\"],\"doc\":\"status, example: '\''ON'\''\"}]}"
}'
```

### Schema com.reservation.domain.avro.v1.ReservationUpdated ###

```
curl --location 'http://localhost:8081/subjects/com.reservation.domain.avro.v1.ReservationUpdated/versions' \
--header 'Content-Type: application/vnd.schemaregistry.v1+json' \
--data '{
    "schema": "{\"type\":\"record\",\"name\":\"ReservationUpdated\",\"namespace\":\"com.reservation.domain.avro.v1\",\"doc\":\"Reservation updated domain event\",\"fields\":[{\"name\":\"id\",\"type\":{\"type\":\"string\",\"logicalType\":\"uuid\"},\"doc\":\"id, example: '\''1fa85f64-5717-4562-b3fc-2c963f66afa6'\''\"},{\"name\":\"version\",\"type\":\"int\"},{\"name\":\"roomTypeId\",\"type\":\"int\",\"doc\":\"roomTypeId, example: '\''1'\''\"},{\"name\":\"hotelId\",\"type\":{\"type\":\"string\",\"logicalType\":\"uuid\"},\"doc\":\"hotelId, example: '\''3fa85f64-5717-4562-b3fc-2c963f66afa6'\''\"},{\"name\":\"guestId\",\"type\":{\"type\":\"string\",\"logicalType\":\"uuid\"},\"doc\":\"guestId, example: '\''4fa85f64-5717-4562-b3fc-2c963f66afa6'\''\"},{\"name\":\"start\",\"type\":\"string\",\"doc\":\"start date, example: '\''2024-05-26'\''\"},{\"name\":\"end\",\"type\":\"string\",\"doc\":\"end date, example: '\''2024-05-26'\''\"},{\"name\":\"status\",\"type\":[\"null\",\"string\"],\"doc\":\"status, example: '\''ON'\''\"}]}"
}'
```

### Schema com.reservation.domain.avro.v1.ReservationDeleted ###

```
curl --location 'http://localhost:8081/subjects/com.reservation.domain.avro.v1.ReservationDeleted/versions' \
--header 'Content-Type: application/vnd.schemaregistry.v1+json' \
--data '{
    "schema": "{\"type\":\"record\",\"name\":\"ReservationDeleted\",\"namespace\":\"com.reservation.domain.avro.v1\",\"doc\":\"Reservation deleted domain event\",\"fields\":[{\"name\":\"id\",\"type\":{\"type\":\"string\",\"logicalType\":\"uuid\"},\"doc\":\"id, example: '\''1fa85f64-5717-4562-b3fc-2c963f66afa6'\''\"}]}"
}'
```

### Schema envelope poc.reservation.domain.v1-value ###

```
curl --location 'http://localhost:8081/subjects/poc.reservation.domain.v1-value/versions' \
--header 'Content-Type: application/vnd.schemaregistry.v1+json' \
--data '{
"subject": "poc.reservation.domain.v1-value",
"references": [
{
"name": "com.reservation.domain.avro.v1.ReservationCreated",
"subject": "com.reservation.domain.avro.v1.ReservationCreated",
"version": 1
},
{
"name": "com.reservation.domain.avro.v1.ReservationUpdated",
"subject": "com.reservation.domain.avro.v1.ReservationUpdated",
"version": 1
},
{
"name": "com.reservation.domain.avro.v1.ReservationDeleted",
"subject": "com.reservation.domain.avro.v1.ReservationDeleted",
"version": 1
}
],
"schema": "[\"com.reservation.domain.avro.v1.ReservationCreated\",\"com.reservation.domain.avro.v1.ReservationUpdated\",\"com.reservation.domain.avro.v1.ReservationDeleted\"]"
}'
```

After creating these schemas you can send different types of domain events to the  poc.reservation.domain.v1 topic previously created by the Kafka docker container.

You can also use this maven plugin to do the same thing:

TODO update with roomTypeInventory schemas

```
<plugin>
  <groupId>io.confluent</groupId>
  <artifactId>kafka-schema-registry-maven-plugin</artifactId>
  <version>${confluent.version}</version>
  <configuration>
      <schemaRegistryUrls>
          <param>http://127.0.0.1:8081</param>
      </schemaRegistryUrls>
      <subjects>
          <poc.reservation.domain.v1-value>${project.basedir}/src/main/resources/asyncapi/v1/envelope/poc.reservation.domain.v1.avsc</poc.reservation.domain.v1-value>
          <com.reservation.domain.avro.v1.ReservationCreated>${project.basedir}/src/main/resources/asyncapi/v1/imports/reservation_created.avsc</com.reservation.domain.avro.v1.ReservationCreated>
          <com.reservation.domain.avro.v1.ReservationUpdated>${project.basedir}/src/main/resources/asyncapi/v1/imports/reservation_updated.avsc</com.reservation.domain.avro.v1.ReservationUpdated>
          <com.reservation.domain.avro.v1.ReservationDeleted>${project.basedir}/src/main/resources/asyncapi/v1/imports/reservation_deleted.avsc</com.reservation.domain.avro.v1.ReservationDeleted>
      </subjects>
      <schemaTypes>
          <poc.reservation.domain.v1-value>AVRO</poc.reservation.domain.v1-value>
          <com.reservation.domain.avro.v1.ReservationCreated>AVRO</com.reservation.domain.avro.v1.ReservationCreated>
          <com.reservation.domain.avro.v1.ReservationUpdated>AVRO</com.reservation.domain.avro.v1.ReservationUpdated>
          <com.reservation.domain.avro.v1.ReservationDeleted>AVRO</com.reservation.domain.avro.v1.ReservationDeleted>
      </schemaTypes>
      <references>
          <poc.reservation.domain.v1-value>
              <reference>
                  <name>com.reservation.domain.avro.v1.ReservationCreated</name>
                  <subject>com.reservation.domain.avro.v1.ReservationCreated</subject>
              </reference>
              <reference>
                  <name>com.reservation.domain.avro.v1.ReservationUpdated</name>
                  <subject>com.reservation.domain.avro.v1.ReservationUpdated</subject>
              </reference>
              <reference>
                  <name>com.reservation.domain.avro.v1.ReservationDeleted</name>
                  <subject>com.reservation.domain.avro.v1.ReservationDeleted</subject>
              </reference>
          </poc.reservation.domain.v1-value>
      </references>
  </configuration>
  <goals>
      <goal>register</goal>
  </goals>
  </plugin>   
```

Run from bc-reservation:

```
mvn clean schema-registry:register
```
