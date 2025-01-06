package com.reservation.domain.usecase;

import com.reservation.domain.model.Reservation;

public interface UpdateReservationCommand {

	void updateReservation(Reservation reservation);
}
