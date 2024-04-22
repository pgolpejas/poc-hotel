package com.outbox.data;

import org.apache.avro.specific.SpecificRecord;

import java.util.Map;

public record OutboxKafkaMessage(SpecificRecord envelope, Map<String, Object> specificHeaders) {
}
