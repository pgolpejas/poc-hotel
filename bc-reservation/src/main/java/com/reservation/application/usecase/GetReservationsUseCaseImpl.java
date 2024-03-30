package com.reservation.application.usecase;

import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.PaginationResponse;
import com.reservation.domain.model.Reservation;
import com.reservation.domain.repository.ReservationRepository;
import com.reservation.domain.usecase.GetReservationsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetReservationsUseCaseImpl implements GetReservationsUseCase {

    private final ReservationRepository reservationRepository;

    @Override
    public PaginationResponse<Reservation> getReservations(final Criteria searchDto) {
        return this.reservationRepository.search(searchDto);
    }
}
