package com.hotel.infrastructure.consumer;

import com.room.domain.avro.v1.RoomCreated;
import com.room.domain.avro.v1.RoomDeleted;
import com.room.domain.avro.v1.RoomUpdated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static com.hotel.core.domain.utils.EventHelper.EVENT_TYPE;

@Component
@Slf4j
@RequiredArgsConstructor
public class RoomDomainConsumer {

    public static final String DOMAIN_INPUT_HEADER_RECEIVED = "[DOMAIN-INPUT-HEADER-RECEIVED]: {}";
    public static final String LOG_INFO_TEMPLATE = "{}: {}";

    @Bean
    public Consumer<Message<RoomCreated>> handleRoomCreatedEvent() {
        return message -> {
            log.info(DOMAIN_INPUT_HEADER_RECEIVED, message.getHeaders());

            log.info(LOG_INFO_TEMPLATE, message.getHeaders().get(EVENT_TYPE), message.getPayload());
        };
    }

    @Bean
    public Consumer<Message<RoomUpdated>> handleRoomUpdatedEvent() {
        return message -> {
            log.info(DOMAIN_INPUT_HEADER_RECEIVED, message.getHeaders());

            log.info(LOG_INFO_TEMPLATE, message.getHeaders().get(EVENT_TYPE), message.getPayload());
        };
    }

    @Bean
    public Consumer<Message<RoomDeleted>> handleRoomDeletedEvent() {
        return message -> {
            log.info(DOMAIN_INPUT_HEADER_RECEIVED, message.getHeaders());

            log.info(LOG_INFO_TEMPLATE, message.getHeaders().get(EVENT_TYPE), message.getPayload());
        };
    }
}
