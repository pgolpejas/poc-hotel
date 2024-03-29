package com.reservation.domain.model;

import com.reservation.domain.core.DomainError;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReservationTest {

    @Test
    void when_reservation_has_incorrect_dates_should_throw_exception() {
        UUID hotelId = UUID.randomUUID();
        Integer roomTypeId = 1;
        UUID guestId = UUID.randomUUID();
        final Reservation reservation = Reservation.builder()
                .id(UUID.randomUUID())
                .hotelId(hotelId)
                .roomTypeId(roomTypeId)
                .guestId(guestId)
                .start(LocalDate.now())
                .end(LocalDate.now().minusDays(1))
                .status("ON")
                .build();
        assertThrows(DomainError.class, () -> {
            Reservation.create(reservation);
        });
    }
}
