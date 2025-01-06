package com.reservation.application.usecase;

import com.reservation.domain.exception.ReservationNotFoundException;
import com.reservation.domain.model.Reservation;
import com.reservation.domain.repository.ReservationRepository;
import com.reservation.domain.usecase.GetReservationQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetReservationQueryHandler implements GetReservationQuery {

	private final ReservationRepository reservationRepository;

	@Override
	public Reservation getReservation(final UUID id) {
		return this.reservationRepository.findById(id)
				.orElseThrow(() -> new ReservationNotFoundException("Reservation with id %s not found", id));
	}
}
