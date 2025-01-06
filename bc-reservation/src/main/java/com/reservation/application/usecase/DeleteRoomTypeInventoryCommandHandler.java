package com.reservation.application.usecase;

import com.reservation.domain.exception.RoomTypeInventoryNotFoundException;
import com.reservation.domain.model.RoomTypeInventory;
import com.reservation.domain.repository.RoomTypeInventoryRepository;
import com.reservation.domain.usecase.DeleteRoomTypeInventoryCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteRoomTypeInventoryCommandHandler implements DeleteRoomTypeInventoryCommand {

	private final RoomTypeInventoryRepository repository;

	@Override
	public void deleteRoomTypeInventory(final UUID id) {
		final Optional<RoomTypeInventory> optionalRoomTypeInventory = this.repository.findById(id);

		if (optionalRoomTypeInventory.isEmpty()) {
			throw new RoomTypeInventoryNotFoundException("RoomTypeInventory with id %s not found", id.toString());
		} else {
			final RoomTypeInventory aggregate = optionalRoomTypeInventory.get();
			aggregate.delete();
			this.repository.delete(aggregate);
		}
	}
}
