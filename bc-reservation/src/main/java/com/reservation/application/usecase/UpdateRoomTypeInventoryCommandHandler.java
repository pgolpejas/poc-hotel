package com.reservation.application.usecase;

import com.reservation.domain.exception.RoomTypeInventoryNotFoundException;
import com.reservation.domain.model.RoomTypeInventory;
import com.reservation.domain.repository.RoomTypeInventoryRepository;
import com.reservation.domain.usecase.UpdateRoomTypeInventoryCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UpdateRoomTypeInventoryCommandHandler implements UpdateRoomTypeInventoryCommand {

	private final RoomTypeInventoryRepository repository;

	@Override
	public void updateRoomTypeInventory(final RoomTypeInventory roomTypeInventory) {

		final Optional<RoomTypeInventory> optionalRoomTypeInventory = this.repository.findById(roomTypeInventory.id());

		if (optionalRoomTypeInventory.isEmpty()) {
			throw new RoomTypeInventoryNotFoundException("RoomTypeInventory with id %s not found",
					roomTypeInventory.id().toString());
		} else {
			final RoomTypeInventory aggregate = optionalRoomTypeInventory.get();
			aggregate.update(roomTypeInventory.totalInventory(), roomTypeInventory.totalReserved());
			this.repository.save(aggregate);
		}
	}
}
