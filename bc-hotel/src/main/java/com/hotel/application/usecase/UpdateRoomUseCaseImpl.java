package com.hotel.application.usecase;

import com.hotel.domain.exception.RoomNotFoundException;
import com.hotel.domain.model.Room;
import com.hotel.domain.repository.RoomRepository;
import com.hotel.domain.usecase.UpdateRoomUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UpdateRoomUseCaseImpl implements UpdateRoomUseCase {

    private final RoomRepository repository;

    @Override
    public void updateRoom(final Room room) {

        Optional<Room> optionalRoom = repository.findById(room.id());

        if (optionalRoom.isEmpty()) {
            throw new RoomNotFoundException("Room with id %s not found", room.id().toString());
        } else {
            Room aggregate = optionalRoom.get();
            aggregate.update(room);
            this.repository.save(aggregate);
        }
    }
}
