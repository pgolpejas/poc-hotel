package com.reservation.infrastructure.consumer;

import static com.hotel.core.domain.utils.EventHelper.EVENT_TYPE;

import java.util.function.Consumer;

import com.reservation.domain.avro.v1.ReservationSnapshot;
import com.reservation.domain.model.AggregatedReservation.Reservation;
import com.reservation.domain.usecase.CreateReservationDataCommand;
import com.reservation.infrastructure.consumer.mapper.ReservationSnapshotEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReservationDataConsumer {

  private final ReservationSnapshotEventMapper reservationSnapshotEventMapper;

  private final CreateReservationDataCommand createReservationDataCommand;

  @Bean
  public Consumer<Message<ReservationSnapshot>> handleReservation() {
    return message -> {
      log.info("[DATA-INPUT-HEADER-RECEIVED]: {}", message.getHeaders());

      final Reservation reservation =
          this.reservationSnapshotEventMapper.mapToReservation(message.getPayload());

      this.createReservationDataCommand.createReservationData(reservation);

      log.info("{}: {}", message.getHeaders().get(EVENT_TYPE), message.getPayload());
    };
  }

}
