package com.reservation.domain.repository;

import com.reservation.domain.model.Reservation;

import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository {

    //Reservations findReservationsByFilters(int page, int limit, final ReservationFilters filters);

    void save(Reservation reservation);

    Optional<Reservation> findById(UUID id);

}
