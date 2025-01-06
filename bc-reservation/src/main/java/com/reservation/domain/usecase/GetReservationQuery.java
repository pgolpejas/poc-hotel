package com.reservation.domain.usecase;

import com.reservation.domain.model.Reservation;

import java.util.UUID;

public interface GetReservationQuery {

	Reservation getReservation(UUID id);
}
