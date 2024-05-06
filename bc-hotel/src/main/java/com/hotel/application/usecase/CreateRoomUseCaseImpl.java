package com.hotel.application.usecase;

import com.hotel.domain.exception.RoomConflictException;
import com.hotel.domain.model.Room;
import com.hotel.domain.repository.RoomRepository;
import com.hotel.domain.usecase.CreateRoomUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@RequiredArgsConstructor
@Validated
public class CreateRoomUseCaseImpl implements CreateRoomUseCase {

    private final RoomRepository repository;

    @Override
    public void createRoom(@Valid final Room room) {
        if (this.repository.existsById(room.id())) {
            throw new RoomConflictException("Room with id %s already exists", room.id().toString());
        }
        if (this.repository.existsByUK(room.hotelId(), room.roomTypeId(), room.floor(), room.roomNumber())) {
            throw new RoomConflictException("Room with hotelId %s, roomTypeId %d, floor %d, roomNumber %s already exists",
                    room.hotelId().toString(), room.roomTypeId(), room.floor(), room.roomNumber());
        }
        
        final Room newRoom = Room.create(room);
        this.repository.save(newRoom);
    }
}
