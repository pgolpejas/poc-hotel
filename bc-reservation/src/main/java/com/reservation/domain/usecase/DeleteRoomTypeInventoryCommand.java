package com.reservation.domain.usecase;

import java.util.UUID;

public interface DeleteRoomTypeInventoryCommand {

	void deleteRoomTypeInventory(UUID id);
}
