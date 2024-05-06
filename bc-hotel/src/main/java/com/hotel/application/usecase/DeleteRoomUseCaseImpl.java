package com.hotel.application.usecase;

import com.hotel.domain.exception.RoomNotFoundException;
import com.hotel.domain.model.Room;
import com.hotel.domain.repository.RoomRepository;
import com.hotel.domain.usecase.DeleteRoomUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteRoomUseCaseImpl implements DeleteRoomUseCase {

    private final RoomRepository repository;

    @Override
    public void deleteRoom(final UUID id) {
        Optional<Room> optionalRoom = repository.findById(id);

        if (optionalRoom.isEmpty()) {
            throw new RoomNotFoundException("Room with id %s not found", id.toString());
        } else {
            Room aggregate = optionalRoom.get();
            aggregate.delete();
            this.repository.delete(aggregate);
        }
    }
}
