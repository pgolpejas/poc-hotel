package com.reservation.application.usecase;

import com.reservation.domain.model.Reservation;
import com.reservation.domain.model.Reservations;
import com.reservation.domain.repository.ReservationRepository;
import com.reservation.domain.usecase.GetReservationsUseCase;
import com.reservation.domain.utils.Criteria;
import com.reservation.domain.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetReservationsUseCaseImpl implements GetReservationsUseCase {

    private final ReservationRepository reservationRepository;

    @Override
    public Reservations getReservations(final Criteria searchDto) {
        final PageResponse<Reservation> pageResponse = this.reservationRepository.search(searchDto);

        return new Reservations(pageResponse.totalItems(), pageResponse.items());
    }
}
