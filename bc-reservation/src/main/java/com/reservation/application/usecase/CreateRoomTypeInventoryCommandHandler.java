package com.reservation.application.usecase;

import com.reservation.domain.exception.RoomTypeInventoryConflictException;
import com.reservation.domain.model.RoomTypeInventory;
import com.reservation.domain.repository.RoomTypeInventoryRepository;
import com.reservation.domain.usecase.CreateRoomTypeInventoryCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@RequiredArgsConstructor
@Validated
public class CreateRoomTypeInventoryCommandHandler implements CreateRoomTypeInventoryCommand {

	private final RoomTypeInventoryRepository repository;

	@Override
	public void createRoomTypeInventory(@Valid final RoomTypeInventory reservation) {
		if (this.repository.existsById(reservation.id())) {
			throw new RoomTypeInventoryConflictException("RoomTypeInventory with id %s already exists",
					reservation.id().toString());
		} else if (this.repository.existsByUK(reservation.hotelId(), reservation.roomTypeId(),
				reservation.roomTypeInventoryDate())) {
			throw new RoomTypeInventoryConflictException(
					"RoomTypeInventory with hotelId %s, roomTypeId %d, inventoryDate %s already exists",
					reservation.hotelId().toString(), reservation.roomTypeId(),
					reservation.roomTypeInventoryDate().toString());
		} else {
			final RoomTypeInventory newRoomTypeInventory = RoomTypeInventory.create(reservation);
			this.repository.save(newRoomTypeInventory);
		}
	}
}
