package com.reservation.application.usecase;

import com.reservation.domain.exception.RoomTypeInventoryNotFoundException;
import com.reservation.domain.model.RoomTypeInventory;
import com.reservation.domain.repository.RoomTypeInventoryRepository;
import com.reservation.domain.usecase.GetRoomTypeInventoryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetRoomTypeInventoryUseCaseImpl implements GetRoomTypeInventoryUseCase {

    private final RoomTypeInventoryRepository repository;

    @Override
    public RoomTypeInventory getRoomTypeInventory(final UUID id) {
        return this.repository.findById(id)
                .orElseThrow(() -> new RoomTypeInventoryNotFoundException("RoomTypeInventory with id %s not found", id));
    }
}
