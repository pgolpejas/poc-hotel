package com.reservation.application.usecase;

import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.reservation.domain.model.RoomTypeInventory;
import com.reservation.domain.repository.RoomTypeInventoryRepository;
import com.reservation.domain.usecase.GetRoomTypeInventoriesAuditQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetRoomTypeInventoriesAuditQueryHandler implements GetRoomTypeInventoriesAuditQuery {

	private final RoomTypeInventoryRepository repository;

	@Override
	public List<RoomTypeInventory> getRoomTypeInventoriesAudit(final AuditFilters filters, final int limit) {
		return this.repository.findAuditByFilters(filters, limit);
	}
}
