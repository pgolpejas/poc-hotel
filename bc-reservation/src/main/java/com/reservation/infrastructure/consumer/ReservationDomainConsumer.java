package com.reservation.infrastructure.consumer;

import static com.hotel.core.domain.utils.EventHelper.EVENT_TYPE;

import java.util.function.Consumer;

import com.reservation.domain.avro.v1.ReservationCreated;
import com.reservation.domain.avro.v1.ReservationDeleted;
import com.reservation.domain.avro.v1.ReservationUpdated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReservationDomainConsumer {

  public static final String DOMAIN_INPUT_HEADER_RECEIVED = "[DOMAIN-INPUT-HEADER-RECEIVED]: {}";
  public static final String LOG_INFO_TEMPLATE = "{}: {}";

  @Bean
  public Consumer<Message<ReservationCreated>> handleReservationCreatedEvent() {
    return message -> {
      log.info(DOMAIN_INPUT_HEADER_RECEIVED, message.getHeaders());

      log.info(LOG_INFO_TEMPLATE, message.getHeaders().get(EVENT_TYPE), message.getPayload());
    };
  }

  @Bean
  public Consumer<Message<ReservationUpdated>> handleReservationUpdatedEvent() {
    return message -> {
      log.info(DOMAIN_INPUT_HEADER_RECEIVED, message.getHeaders());

      log.info(LOG_INFO_TEMPLATE, message.getHeaders().get(EVENT_TYPE), message.getPayload());
    };
  }

  @Bean
  public Consumer<Message<ReservationDeleted>> handleReservationDeletedEvent() {
    return message -> {
      log.info(DOMAIN_INPUT_HEADER_RECEIVED, message.getHeaders());

      log.info(LOG_INFO_TEMPLATE, message.getHeaders().get(EVENT_TYPE), message.getPayload());
    };
  }
}
