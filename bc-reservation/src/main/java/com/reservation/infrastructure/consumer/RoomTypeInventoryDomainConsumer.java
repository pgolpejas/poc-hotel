package com.reservation.infrastructure.consumer;

import com.roomTypeInventory.domain.avro.v1.RoomTypeInventoryCreated;
import com.roomTypeInventory.domain.avro.v1.RoomTypeInventoryDeleted;
import com.roomTypeInventory.domain.avro.v1.RoomTypeInventoryUpdated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@Slf4j
@RequiredArgsConstructor
public class RoomTypeInventoryDomainConsumer {

    public static final String DOMAIN_INPUT_HEADER_RECEIVED = "[DOMAIN-INPUT-HEADER-RECEIVED]: {}";
    public static final String LOG_INFO_TEMPLATE = "{}: {}";

    @Bean
    public Consumer<Message<RoomTypeInventoryCreated>> handleRoomTypeInventoryCreatedEvent() {
        return message -> {
            log.info(DOMAIN_INPUT_HEADER_RECEIVED, message.getHeaders());

            log.info(LOG_INFO_TEMPLATE, message.getHeaders().get("event_type"), message.getPayload());
        };
    }

    @Bean
    public Consumer<Message<RoomTypeInventoryUpdated>> handleRoomTypeInventoryUpdatedEvent() {
        return message -> {
            log.info(DOMAIN_INPUT_HEADER_RECEIVED, message.getHeaders());

            log.info(LOG_INFO_TEMPLATE, message.getHeaders().get("event_type"), message.getPayload());
        };
    }

    @Bean
    public Consumer<Message<RoomTypeInventoryDeleted>> handleRoomTypeInventoryDeletedEvent() {
        return message -> {
            log.info(DOMAIN_INPUT_HEADER_RECEIVED, message.getHeaders());

            log.info(LOG_INFO_TEMPLATE, message.getHeaders().get("event_type"), message.getPayload());
        };
    }
}
