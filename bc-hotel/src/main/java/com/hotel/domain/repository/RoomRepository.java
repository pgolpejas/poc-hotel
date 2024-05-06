package com.hotel.domain.repository;

import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.domain.repository.BaseAuditRepository;
import com.hotel.core.domain.repository.BaseRepository;
import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.domain.model.Room;

import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends BaseRepository<Room, UUID>, BaseAuditRepository<Room, AuditFilters> {

    boolean existsByUK(UUID hotelId, Integer roomTypeId, int floor, String roomNumber);

    Optional<Room> findByUK(UUID hotelId, Integer roomTypeId, int floor, String roomNumber);

    PaginationResponse<Room> searchBySelection(Criteria criteria);
}
