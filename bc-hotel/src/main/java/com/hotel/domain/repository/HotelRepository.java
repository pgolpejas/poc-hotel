package com.hotel.domain.repository;

import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.domain.repository.BaseAuditRepository;
import com.hotel.core.domain.repository.BaseRepository;
import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.domain.model.Hotel;

import java.util.UUID;

public interface HotelRepository extends BaseRepository<Hotel, UUID>, BaseAuditRepository<Hotel, AuditFilters> {

    PaginationResponse<Hotel> searchBySelection(Criteria criteria);

    boolean existsByUK(String name);
}
