package com.outbox.data.tabledescriptors;

import com.outbox.data.BaseEntityFieldDescriptor;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public abstract class BaseTableDescriptor {

    private static final String ID = "id";

    private static final String CREATED_AT = "created_at";

    private static final String PUBLISHED_AT = "published_at";

    private static final String AGGREGATE_TYPE = "aggregate_type";

    private static final String OUTBOX_EV_TYPE = "outbox_event_type";

    private static final String STRATEGY_TYPE = "strategy_type";

    private static final String AGGREGATE_ID = "aggregate_id";

    private static final String PAYLOAD = "payload";

    private static final String VERSION = "version";


    private static final String HEADERS = "headers";

    private static final String ERROR_MESSAGE = "error_message";

    private static final String RETRYABLE = "retryable";

    private static final String RETRIES = "retries";
    
    private static final String DESTINATION_TOPIC = "destination_topic";

    private List<BaseEntityFieldDescriptor> descriptors;

    private BaseEntityFieldDescriptor idCol;

    private BaseEntityFieldDescriptor createdCol;

    private BaseEntityFieldDescriptor publishedCol;

    private BaseEntityFieldDescriptor strategyCol;

    private BaseEntityFieldDescriptor aggregateTypeCol;

    private BaseEntityFieldDescriptor aggregateIdCol;

    private BaseEntityFieldDescriptor payloadCol;

    private BaseEntityFieldDescriptor versionCol;

    private BaseEntityFieldDescriptor headersCol;

    private BaseEntityFieldDescriptor errorCol;

    private BaseEntityFieldDescriptor retriesCol;

    private BaseEntityFieldDescriptor retryableCol;

    private BaseEntityFieldDescriptor outboxEvTypeCol;

    private BaseEntityFieldDescriptor aggregateRootCol;

    private BaseEntityFieldDescriptor jsonSourceCol;
    
    private BaseEntityFieldDescriptor destinationTopicCol;

    protected BaseTableDescriptor() {

        descriptors = new ArrayList<>();
        idCol = BaseEntityFieldDescriptor.builder()
                .mongoColumnName("_id")
                .jdbcColumnName(ID)
                .fieldName(ID)
                .type(String.class)
                .updatable(false)
                .primary(true)
                .build();
        createdCol = BaseEntityFieldDescriptor.builder()
                .mongoColumnName(CREATED_AT)
                .jdbcColumnName(CREATED_AT)
                .fieldName("createdAt")
                .type(Timestamp.class)
                .updatable(false)
                .primary(false)
                .build();
        publishedCol = BaseEntityFieldDescriptor.builder()
                .mongoColumnName(PUBLISHED_AT)
                .jdbcColumnName(PUBLISHED_AT)
                .fieldName("publishedAt")
                .type(Timestamp.class)
                .updatable(true)
                .primary(false)
                .build();
        aggregateTypeCol = BaseEntityFieldDescriptor.builder()
                .mongoColumnName(AGGREGATE_TYPE)
                .jdbcColumnName(AGGREGATE_TYPE)
                .fieldName("aggregateType")
                .type(String.class)
                .updatable(false)
                .primary(false)
                .build();
        outboxEvTypeCol = BaseEntityFieldDescriptor.builder()
                .mongoColumnName(OUTBOX_EV_TYPE)
                .jdbcColumnName(OUTBOX_EV_TYPE)
                .fieldName("outboxEventType")
                .type(String.class)
                .updatable(true)
                .primary(false)
                .build();
        strategyCol = BaseEntityFieldDescriptor.builder()
                .mongoColumnName(STRATEGY_TYPE)
                .jdbcColumnName(STRATEGY_TYPE)
                .fieldName("sendStrategyType")
                .type(String.class)
                .updatable(false)
                .primary(false)
                .build();

        aggregateRootCol = BaseEntityFieldDescriptor.builder()
                .mongoColumnName("aggregate_root")
                .jdbcColumnName("aggregate_root")
                .fieldName("aggregateRoot")
                .type(String.class)
                .updatable(false)
                .primary(false)
                .build();

        aggregateIdCol = BaseEntityFieldDescriptor.builder()
                .mongoColumnName(AGGREGATE_ID)
                .jdbcColumnName(AGGREGATE_ID)
                .fieldName("aggregateId")
                .type(String.class)
                .updatable(false)
                .primary(false)
                .build();

        payloadCol = BaseEntityFieldDescriptor.builder()
                .mongoColumnName(PAYLOAD)
                .jdbcColumnName(PAYLOAD)
                .fieldName("serializedEvent")
                .type(byte[].class)
                .updatable(true)
                .primary(false)
                .build();

        versionCol = BaseEntityFieldDescriptor.builder()
                .mongoColumnName(VERSION)
                .jdbcColumnName(VERSION)
                .fieldName(VERSION)
                .type(String.class)
                .updatable(true)
                .primary(false)
                .build();


        headersCol = BaseEntityFieldDescriptor.builder()
                .mongoColumnName(HEADERS)
                .jdbcColumnName(HEADERS)
                .fieldName("headersMap")
                .type(String.class)
                .updatable(true)
                .primary(false)
                .build();

        errorCol = BaseEntityFieldDescriptor.builder()
                .mongoColumnName(ERROR_MESSAGE)
                .jdbcColumnName(ERROR_MESSAGE)
                .fieldName("errorMessage")
                .type(String.class)
                .updatable(true)
                .primary(false)
                .build();

        retriesCol = BaseEntityFieldDescriptor.builder()
                .mongoColumnName(RETRIES)
                .jdbcColumnName(RETRIES)
                .fieldName(RETRIES)
                .type(Integer.class)
                .updatable(true)
                .primary(false)
                .build();

        retryableCol = BaseEntityFieldDescriptor.builder()
                .mongoColumnName(RETRYABLE)
                .jdbcColumnName(RETRYABLE)
                .fieldName(RETRYABLE)
                .type(Boolean.class)
                .updatable(true)
                .primary(false)
                .build();

        jsonSourceCol = BaseEntityFieldDescriptor.builder()
                .mongoColumnName("json_source")
                .jdbcColumnName("json_source")
                .fieldName("jsonSource")
                .type(String.class)
                .updatable(false)
                .primary(false)
                .build();

        destinationTopicCol = BaseEntityFieldDescriptor.builder()
                .mongoColumnName("destination_topic")
                .jdbcColumnName("destination_topic")
                .fieldName("destinationTopic")
                .type(String.class)
                .updatable(false)
                .primary(false)
                .build();

        getDescriptors().add(idCol);
        getDescriptors().add(createdCol);
        getDescriptors().add(publishedCol);
        getDescriptors().add(aggregateTypeCol);
        getDescriptors().add(strategyCol);
        getDescriptors().add(aggregateRootCol);
        getDescriptors().add(payloadCol);
        getDescriptors().add(versionCol);
        getDescriptors().add(aggregateIdCol);
        getDescriptors().add(headersCol);
        getDescriptors().add(outboxEvTypeCol);
        getDescriptors().add(errorCol);
        getDescriptors().add(retriesCol);
        getDescriptors().add(retryableCol);
        getDescriptors().add(jsonSourceCol);
        getDescriptors().add(destinationTopicCol);
    }

    public abstract String getTableName();

}

