package com.hotel.domain.usecase;

import java.util.UUID;

import com.hotel.domain.model.Room;

public interface GetRoomQuery {

  Room getRoom(UUID id);
}
