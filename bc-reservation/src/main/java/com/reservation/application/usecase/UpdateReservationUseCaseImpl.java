package com.reservation.application.usecase;

import com.reservation.domain.exception.ReservationNotFoundException;
import com.reservation.domain.model.Reservation;
import com.reservation.domain.repository.ReservationRepository;
import com.reservation.domain.usecase.UpdateReservationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UpdateReservationUseCaseImpl implements UpdateReservationUseCase {

    private final ReservationRepository reservationRepository;

    @Override
    public void updateReservation(final Reservation reservation) {

        Optional<Reservation> optionalReservation = reservationRepository.findById(reservation.id());

        if (optionalReservation.isEmpty()) {
            throw new ReservationNotFoundException("Reservation with id %s not found", reservation.id().toString());
        } else {
            Reservation aggregate = optionalReservation.get();
            aggregate.update(reservation);
            this.reservationRepository.save(aggregate);
        }
    }
}
