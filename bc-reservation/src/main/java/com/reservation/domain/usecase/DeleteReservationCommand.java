package com.reservation.domain.usecase;

import java.util.UUID;

public interface DeleteReservationCommand {

	void deleteReservation(UUID id);
}
