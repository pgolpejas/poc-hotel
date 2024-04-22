package com.reservation.application.usecase;

import com.reservation.domain.exception.ReservationConflictException;
import com.reservation.domain.model.Reservation;
import com.reservation.domain.repository.ReservationRepository;
import com.reservation.domain.usecase.CreateReservationUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@RequiredArgsConstructor
@Validated
public class CreateReservationUseCaseImpl implements CreateReservationUseCase {

    private final ReservationRepository reservationRepository;

    @Override
    public void createReservation(@Valid final Reservation reservation) {

        if (this.reservationRepository.existsById(reservation.id())) {
            throw new ReservationConflictException("Reservation with id %s already exists", reservation.id().toString());
        } else {
            final Reservation newReservation = Reservation.create(reservation);
            this.reservationRepository.save(newReservation);
        }
    }
}
