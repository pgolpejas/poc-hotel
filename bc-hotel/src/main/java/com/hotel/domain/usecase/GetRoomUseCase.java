package com.hotel.domain.usecase;

import com.hotel.domain.model.Room;

import java.util.UUID;

public interface GetRoomUseCase {

    Room getRoom(UUID id);
}
