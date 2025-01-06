package com.reservation.domain.usecase;

import java.util.UUID;

import com.reservation.domain.model.Room;
import org.springframework.validation.annotation.Validated;

@Validated
public interface CreateRoomDataCommand {

  void createRoomData(Room room, UUID hotelId);
}
