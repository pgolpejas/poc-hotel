package com.reservation.domain.usecase;

import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.PaginationResponse;
import com.reservation.domain.model.RoomTypeInventory;

public interface GetRoomTypeInventoriesUseCase {

    PaginationResponse<RoomTypeInventory> getRoomTypeInventories(Criteria searchDto);
}
