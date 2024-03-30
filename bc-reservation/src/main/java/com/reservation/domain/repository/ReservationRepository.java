package com.reservation.domain.repository;

import com.reservation.domain.model.Reservation;
import com.reservation.domain.utils.Criteria;
import com.reservation.domain.utils.PageResponse;

import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository {

    PageResponse<Reservation> search(Criteria criteria);

    PageResponse<Reservation> searchBySelection(Criteria criteria);

    void save(Reservation reservation);

    Optional<Reservation> findById(UUID id);

}
