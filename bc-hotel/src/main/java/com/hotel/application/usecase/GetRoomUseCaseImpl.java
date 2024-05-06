package com.hotel.application.usecase;

import com.hotel.domain.exception.RoomNotFoundException;
import com.hotel.domain.model.Room;
import com.hotel.domain.repository.RoomRepository;
import com.hotel.domain.usecase.GetRoomUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetRoomUseCaseImpl implements GetRoomUseCase {

    private final RoomRepository repository;

    @Override
    public Room getRoom(final UUID id) {
        return this.repository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room with id %s not found", id));
    }
}
