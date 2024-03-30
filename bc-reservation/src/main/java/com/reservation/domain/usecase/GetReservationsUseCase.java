package com.reservation.domain.usecase;

import com.hotel.core.domain.dto.PaginationResponse;
import com.reservation.domain.model.Reservation;
import com.hotel.core.domain.dto.Criteria;

public interface GetReservationsUseCase {

    PaginationResponse<Reservation> getReservations(Criteria searchDto);
}
