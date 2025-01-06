package com.reservation.domain.usecase;

import com.reservation.domain.model.RoomTypeInventory;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

@Validated
public interface CreateRoomTypeInventoryCommand {

	void createRoomTypeInventory(@Valid RoomTypeInventory roomTypeInventory);
}
