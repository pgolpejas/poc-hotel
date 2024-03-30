package com.reservation.domain.repository;

import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.domain.repository.BaseAuditRepository;
import com.hotel.core.domain.repository.BaseRepository;
import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.reservation.domain.model.Reservation;

public interface ReservationRepository extends BaseRepository<Reservation>, BaseAuditRepository<Reservation, AuditFilters> {

    PaginationResponse<Reservation> searchBySelection(Criteria criteria);
}
