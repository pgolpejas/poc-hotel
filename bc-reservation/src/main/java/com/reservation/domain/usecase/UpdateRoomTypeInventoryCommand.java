package com.reservation.domain.usecase;

import com.reservation.domain.model.RoomTypeInventory;

public interface UpdateRoomTypeInventoryCommand {

	void updateRoomTypeInventory(RoomTypeInventory roomTypeInventory);
}
