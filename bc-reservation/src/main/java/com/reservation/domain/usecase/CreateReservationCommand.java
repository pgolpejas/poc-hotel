package com.reservation.domain.usecase;

import com.reservation.domain.model.Reservation;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

@Validated
public interface CreateReservationCommand {

  void createReservation(@Valid Reservation reservation);
}
