package com.reservation.domain.repository;

import com.reservation.domain.model.Reservation;
import com.reservation.utils.BaseTestContainer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservationRepositoryIT extends BaseTestContainer {

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    void when_reservation_is_ok_should_create_it() {
        UUID hotelId = UUID.randomUUID();
        Integer roomTypeId = 1;
        UUID guestId = UUID.randomUUID();
        final Reservation reservation = Reservation.builder()
                .id(UUID.randomUUID())
                .hotelId(hotelId)
                .roomTypeId(roomTypeId)
                .guestId(guestId)
                .start(LocalDate.now())
                .end(LocalDate.now())
                .status("ON")
                .build();
        reservationRepository.save(reservation);

        Optional<Reservation> optSearch = reservationRepository.findById(reservation.getId().value());

        assertTrue(optSearch.isPresent());
        Assertions.assertThat(optSearch.get().toString()).as("toString").isNotBlank();
        Assertions.assertThat(optSearch.get().id()).as("id").isEqualTo(reservation.id());
        Assertions.assertThat(optSearch.get().id().toString()).as("aggregateId").hasToString(reservation.getAggregateId());
        Assertions.assertThat(optSearch.get().hotelId()).as("hotelId").isEqualTo(hotelId);
        Assertions.assertThat(optSearch.get().roomTypeId()).as("roomTypeId").isEqualTo(roomTypeId);
        Assertions.assertThat(optSearch.get().guestId()).as("guestId").isEqualTo(guestId);
    }

}
