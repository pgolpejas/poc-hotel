package com.reservation.application.usecase;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.hotel.core.domain.dto.PaginationResponse;
import com.reservation.domain.dto.AggregatedReservationCriteria;
import com.reservation.domain.model.AggregatedReservation;
import com.reservation.domain.model.Hotel;
import com.reservation.domain.model.Room;
import com.reservation.domain.repository.AggregatedReservationRepository;
import com.reservation.domain.repository.HotelRepository;
import com.reservation.domain.usecase.CreateRoomDataCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateRoomDataCommandHandler implements CreateRoomDataCommand {

  private final AggregatedReservationRepository aggregatedReservationRepository;

  private final HotelRepository hotelRepository;

  @Override
  public void createRoomData(final Room room, final UUID hotelId) {
    final Optional<Hotel> optHotel = this.hotelRepository.findById(hotelId);
    optHotel.ifPresent(hotel -> {
      hotel.upsertRoom(room);
      this.hotelRepository.save(hotel);

      final PaginationResponse<AggregatedReservation> paginationResponse =
          this.aggregatedReservationRepository.searchBySelection(AggregatedReservationCriteria.builder()
              .hotelIds(Set.of(hotelId))
              .page(0)
              .limit(-1)
              .sortDirection("ASC")
              .sortBy("start_date")
              .build());
      paginationResponse.data().forEach(aggregatedReservation -> {
        aggregatedReservation.updateHotel(hotel);
        this.aggregatedReservationRepository.update(aggregatedReservation);
      });
    });
  }
}
