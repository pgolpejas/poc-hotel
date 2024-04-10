package com.reservation.infrastructure.consumer;

import com.reservation.domain.avro.v1.ReservationCreated;
import com.reservation.domain.avro.v1.ReservationDeleted;
import com.reservation.domain.avro.v1.ReservationUpdated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@SuppressWarnings("java:S6830")
@Component("reservation-domain")
@RequiredArgsConstructor
@Slf4j
public class ReservationConsumer implements Consumer<Message<Object>> {
    private static final String EVENT_HAS_ALREADY_CONSUMED = "Event has already consumed";

    @Override
    public void accept(final Message<Object> message) {
        log.info("[INPUT-HEADER-RECEIVED]: {}", message.getHeaders());

        switch (message.getPayload()) {
            case ReservationCreated reservationCreatedEvent ->
                    log.info("ReservationCreated: {}", reservationCreatedEvent);

            case ReservationUpdated reservationUpdatedEvent ->
                    log.info("ReservationUpdated: {}", reservationUpdatedEvent);

            case ReservationDeleted reservationDeletedEvent ->
                    log.info("ReservationDeleted: {}", reservationDeletedEvent);
            
            default -> throw new IllegalStateException("Unexpected value: " + message.getPayload());
        }
    }
}
