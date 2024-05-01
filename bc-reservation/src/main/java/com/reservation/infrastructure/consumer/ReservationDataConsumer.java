package com.reservation.infrastructure.consumer;

import static com.hotel.core.domain.utils.EventHelper.EVENT_TYPE;

import java.util.function.Consumer;

import com.reservation.domain.avro.v1.ReservationSnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReservationDataConsumer {

  @Bean
  public Consumer<Message<ReservationSnapshot>> handleReservation() {
    return message -> {
      log.info("[DATA-INPUT-HEADER-RECEIVED]: {}", message.getHeaders());

      log.info("{}: {}", message.getHeaders().get(EVENT_TYPE), message.getPayload());
    };
  }

}
