package com.reservation.application.usecase;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.hotel.core.domain.dto.PaginationResponse;
import com.reservation.domain.dto.AggregatedReservationCriteria;
import com.reservation.domain.model.AggregatedReservation;
import com.reservation.domain.model.Hotel;
import com.reservation.domain.repository.AggregatedReservationRepository;
import com.reservation.domain.repository.HotelRepository;
import com.reservation.domain.usecase.CreateHotelDataCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateHotelDataCommandHandler implements CreateHotelDataCommand {
  private final AggregatedReservationRepository aggregatedReservationRepository;

  private final HotelRepository hotelRepository;

  @Override
  public void createHotelData(final Hotel hotel) {
    final StopWatch stopWatch = new StopWatch();

    stopWatch.start("redis-save");
    final Optional<Hotel> optHotel = this.hotelRepository.findById(hotel.getId());
    final Hotel hotelToUpdate = optHotel.orElse(hotel);
    hotelToUpdate.updateHotel(hotel);
    this.hotelRepository.save(hotelToUpdate);

    stopWatch.stop();

    stopWatch.start("reservation-upsert");
    final PaginationResponse<AggregatedReservation> paginationResponse =
        this.aggregatedReservationRepository.searchBySelection(AggregatedReservationCriteria.builder()
            .hotelIds(Set.of(hotelToUpdate.getId()))
            .page(0)
            .limit(-1)
            .sortDirection("ASC")
            .sortBy("start_date")
            .build());
    paginationResponse.data().forEach(aggregatedReservation -> {
      aggregatedReservation.updateHotel(hotelToUpdate);
      this.aggregatedReservationRepository.update(aggregatedReservation);
    });

    stopWatch.stop();
    log.atInfo().log(() -> stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
  }
}
