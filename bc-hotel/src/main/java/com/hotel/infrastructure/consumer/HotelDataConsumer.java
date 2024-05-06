package com.hotel.infrastructure.consumer;

import com.hotel.domain.avro.v1.HotelSnapshot;
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
public class HotelDataConsumer {

    @Bean
    public Consumer<Message<HotelSnapshot>> handleHotel() {
        return message -> {
            log.info("[DATA-INPUT-HEADER-RECEIVED]: {}", message.getHeaders());

            log.info("{}: {}", message.getHeaders().get(EVENT_TYPE), message.getPayload());
        };
    }

}
