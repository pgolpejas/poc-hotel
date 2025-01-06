package com.reservation.application.usecase;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.reservation.domain.model.AggregatedReservation;
import com.reservation.domain.model.AggregatedReservation.Reservation;
import com.reservation.domain.model.Hotel;
import com.reservation.domain.repository.AggregatedReservationRepository;
import com.reservation.domain.repository.HotelRepository;
import com.reservation.domain.usecase.CreateReservationDataCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateReservationDataCommandHandler implements CreateReservationDataCommand {

  private final AggregatedReservationRepository aggregatedReservationRepository;

  private final HotelRepository hotelRepository;

  @Transactional
  @Override
  public void createReservationData(final Reservation reservation) {
    final StopWatch stopWatch = new StopWatch();

    stopWatch.start("redis-findById");
    final Hotel hotel = this.hotelRepository.findById(reservation.hotel().id())
        .orElse(Hotel.builder()
            .id(reservation.hotel().id())
            .build());

    final Optional<AggregatedReservation> optAggregatedReservation = this.aggregatedReservationRepository.findById(reservation.id());
    optAggregatedReservation.ifPresentOrElse(aggregatedReservation -> {
      aggregatedReservation.updateReservation(reservation.withHotel(hotel));
      this.aggregatedReservationRepository.update(aggregatedReservation);

      // update in Mongo
      this.aggregatedReservationRepository.saveMongo(aggregatedReservation);
    }, () -> {

      final AggregatedReservation aggregatedReservation =
          AggregatedReservation.create(reservation.withHotel(hotel));
      this.aggregatedReservationRepository.save(aggregatedReservation);

      // save in Mongo
      this.aggregatedReservationRepository.saveMongo(aggregatedReservation);
    });
    stopWatch.stop();

    log.atInfo().log(() -> stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
  }
}
