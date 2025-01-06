package com.hotel.application.usecase;

import java.util.Optional;
import java.util.UUID;

import com.hotel.domain.exception.RoomNotFoundException;
import com.hotel.domain.model.Room;
import com.hotel.domain.repository.RoomRepository;
import com.hotel.domain.usecase.DeleteRoomCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteRoomCommandHandler implements DeleteRoomCommand {

  private final RoomRepository repository;

  @Override
  public void deleteRoom(final UUID id) {
    final Optional<Room> optionalRoom = this.repository.findById(id);

    if (optionalRoom.isEmpty()) {
      throw new RoomNotFoundException("Room with id %s not found", id.toString());
    } else {
      final Room aggregate = optionalRoom.get();
      aggregate.delete();
      this.repository.delete(aggregate);
    }
  }
}
