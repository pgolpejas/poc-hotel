package com.reservation.domain.usecase;

import com.reservation.domain.model.Reservations;
import com.reservation.domain.utils.Criteria;

public interface GetReservationsUseCase {

    Reservations getReservations(Criteria searchDto);
}
