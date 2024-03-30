package com.reservation.domain.repository;

import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.core.domain.repository.BaseRepository;
import com.reservation.domain.model.Reservation;

public interface ReservationRepository extends BaseRepository<Reservation, AuditFilters> {
}
