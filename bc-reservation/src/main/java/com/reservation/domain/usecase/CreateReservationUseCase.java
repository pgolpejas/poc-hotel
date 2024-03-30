package com.reservation.domain.usecase;

import com.reservation.domain.model.Reservation;

public interface CreateReservationUseCase {

    void createReservation(Reservation reservation);
}
