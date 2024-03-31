package com.reservation.domain.repository;

import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.domain.repository.BaseAuditRepository;
import com.hotel.core.domain.repository.BaseRepository;
import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.reservation.domain.model.Reservation;

import java.util.UUID;

public interface ReservationRepository extends BaseRepository<Reservation, UUID>, BaseAuditRepository<Reservation, AuditFilters> {

    PaginationResponse<Reservation> searchBySelection(Criteria criteria);
}
