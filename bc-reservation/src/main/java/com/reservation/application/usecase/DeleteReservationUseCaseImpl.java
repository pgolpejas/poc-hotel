package com.reservation.application.usecase;

import com.reservation.domain.exception.ReservationNotFoundException;
import com.reservation.domain.model.Reservation;
import com.reservation.domain.repository.ReservationRepository;
import com.reservation.domain.usecase.DeleteReservationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteReservationUseCaseImpl implements DeleteReservationUseCase {

    private final ReservationRepository reservationRepository;

    @Override
    public void deleteReservation(final UUID id) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(id);

        if (optionalReservation.isEmpty()) {
            throw new ReservationNotFoundException(String.format("Reservation with id %s not found", id.toString()));
        } else {
            Reservation aggregate = optionalReservation.get();
            aggregate.delete();
            this.reservationRepository.delete(aggregate);
        }
    }
}
