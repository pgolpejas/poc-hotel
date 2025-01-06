package com.hotel.application.usecase;

import java.util.Optional;

import com.hotel.domain.exception.RoomNotFoundException;
import com.hotel.domain.model.Room;
import com.hotel.domain.repository.RoomRepository;
import com.hotel.domain.usecase.UpdateRoomCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateRoomCommandHandler implements UpdateRoomCommand {

  private final RoomRepository repository;

  @Override
  public void updateRoom(final Room room) {

    final Optional<Room> optionalRoom = this.repository.findById(room.id());

    if (optionalRoom.isEmpty()) {
      throw new RoomNotFoundException("Room with id %s not found", room.id().toString());
    } else {
      final Room aggregate = optionalRoom.get();
      aggregate.update(room);
      this.repository.save(aggregate);
    }
  }
}
