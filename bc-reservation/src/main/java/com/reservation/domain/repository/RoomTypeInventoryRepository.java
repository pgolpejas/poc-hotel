package com.reservation.domain.repository;

import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.domain.repository.BaseAuditRepository;
import com.hotel.core.domain.repository.BaseRepository;
import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.reservation.domain.model.RoomTypeInventory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomTypeInventoryRepository extends BaseRepository<RoomTypeInventory, UUID>, BaseAuditRepository<RoomTypeInventory, AuditFilters> {

    boolean existsByUK(UUID hotelId, Integer roomTypeId, LocalDate roomTypeInventoryDate);

    Optional<RoomTypeInventory> findByUK(UUID hotelId, Integer roomTypeId, LocalDate roomTypeInventoryDate);
    
    List<RoomTypeInventory> findByReservationDates(UUID hotelId, Integer roomTypeId, LocalDate start, LocalDate end);

    PaginationResponse<RoomTypeInventory> searchBySelection(Criteria criteria);
}
