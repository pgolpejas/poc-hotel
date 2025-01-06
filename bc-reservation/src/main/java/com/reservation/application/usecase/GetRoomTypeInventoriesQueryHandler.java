package com.reservation.application.usecase;

import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.PaginationResponse;
import com.reservation.domain.model.RoomTypeInventory;
import com.reservation.domain.repository.RoomTypeInventoryRepository;
import com.reservation.domain.usecase.GetRoomTypeInventoriesQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetRoomTypeInventoriesQueryHandler implements GetRoomTypeInventoriesQuery {

	private final RoomTypeInventoryRepository repository;

	@Override
	public PaginationResponse<RoomTypeInventory> getRoomTypeInventories(final Criteria searchDto) {
		return this.repository.search(searchDto);
	}
}
