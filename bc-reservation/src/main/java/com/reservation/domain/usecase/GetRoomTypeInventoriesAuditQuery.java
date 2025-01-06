package com.reservation.domain.usecase;

import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.reservation.domain.model.RoomTypeInventory;

import java.util.List;

public interface GetRoomTypeInventoriesAuditQuery {

	List<RoomTypeInventory> getRoomTypeInventoriesAudit(AuditFilters filters, int limit);
}
