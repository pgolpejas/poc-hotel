package com.reservation.domain.usecase;

import com.reservation.domain.model.RoomTypeInventory;

import java.util.UUID;

public interface GetRoomTypeInventoryUseCase {

    RoomTypeInventory getRoomTypeInventory(UUID id);
}
