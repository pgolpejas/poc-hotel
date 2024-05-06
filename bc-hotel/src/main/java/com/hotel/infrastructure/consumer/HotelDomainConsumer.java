package com.hotel.infrastructure.consumer;

import com.hotel.domain.avro.v1.HotelCreated;
import com.hotel.domain.avro.v1.HotelDeleted;
import com.hotel.domain.avro.v1.HotelUpdated;
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
public class HotelDomainConsumer {

    public static final String DOMAIN_INPUT_HEADER_RECEIVED = "[DOMAIN-INPUT-HEADER-RECEIVED]: {}";
    public static final String LOG_INFO_TEMPLATE = "{}: {}";

    @Bean
    public Consumer<Message<HotelCreated>> handleHotelCreatedEvent() {
        return message -> {
            log.info(DOMAIN_INPUT_HEADER_RECEIVED, message.getHeaders());

            log.info(LOG_INFO_TEMPLATE, message.getHeaders().get(EVENT_TYPE), message.getPayload());
        };
    }

    @Bean
    public Consumer<Message<HotelUpdated>> handleHotelUpdatedEvent() {
        return message -> {
            log.info(DOMAIN_INPUT_HEADER_RECEIVED, message.getHeaders());

            log.info(LOG_INFO_TEMPLATE, message.getHeaders().get(EVENT_TYPE), message.getPayload());
        };
    }

    @Bean
    public Consumer<Message<HotelDeleted>> handleHotelDeletedEvent() {
        return message -> {
            log.info(DOMAIN_INPUT_HEADER_RECEIVED, message.getHeaders());

            log.info(LOG_INFO_TEMPLATE, message.getHeaders().get(EVENT_TYPE), message.getPayload());
        };
    }
}
