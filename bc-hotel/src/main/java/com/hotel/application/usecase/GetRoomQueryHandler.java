package com.hotel.application.usecase;

import java.util.UUID;

import com.hotel.domain.exception.RoomNotFoundException;
import com.hotel.domain.model.Room;
import com.hotel.domain.repository.RoomRepository;
import com.hotel.domain.usecase.GetRoomQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetRoomQueryHandler implements GetRoomQuery {

  private final RoomRepository repository;

  @Override
  public Room getRoom(final UUID id) {
    return this.repository.findById(id)
        .orElseThrow(() -> new RoomNotFoundException("Room with id %s not found", id));
  }
}
