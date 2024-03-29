package com.reservation.application.validator;

import com.reservation.application.dto.ReservationDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class DateRangeValidatorTest {

    @InjectMocks
    private DateRangeValidator dateRangeValidator;

    @Test
    void when_reservation_is_ok_reservation_is_valid() {

        final ReservationDto reservationDTO = ReservationDto.builder()
                .id(UUID.randomUUID())
                .hotelId(UUID.randomUUID())
                .roomTypeId(1)
                .guestId(UUID.randomUUID())
                .start(LocalDate.now())
                .end(LocalDate.now())
                .status("ON")
                .build();
        assertTrue(dateRangeValidator.isValid(reservationDTO, null));
    }

    @Test
    void when_reservation_dates_are_incorrect_reservation_is_not_valid() {

        final ReservationDto reservationDTO = ReservationDto.builder()
                .id(UUID.randomUUID())
                .hotelId(UUID.randomUUID())
                .roomTypeId(1)
                .guestId(UUID.randomUUID())
                .start(LocalDate.now())
                .end(LocalDate.now().minusDays(1))
                .status("ON")
                .build();
        assertFalse(dateRangeValidator.isValid(reservationDTO, null));
    }

    @Test
    void when_dates_are_null_reservation_is_valid() {

        final ReservationDto reservationDTO = ReservationDto.builder()
                .id(UUID.randomUUID())
                .hotelId(UUID.randomUUID())
                .roomTypeId(1)
                .guestId(UUID.randomUUID())
                .status("ON")
                .build();
        assertTrue(dateRangeValidator.isValid(reservationDTO, null));
    }

}
