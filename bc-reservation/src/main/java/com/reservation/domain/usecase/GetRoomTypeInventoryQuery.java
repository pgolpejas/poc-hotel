package com.reservation.domain.usecase;

import com.reservation.domain.model.RoomTypeInventory;

import java.util.UUID;

public interface GetRoomTypeInventoryQuery {

	RoomTypeInventory getRoomTypeInventory(UUID id);
}
