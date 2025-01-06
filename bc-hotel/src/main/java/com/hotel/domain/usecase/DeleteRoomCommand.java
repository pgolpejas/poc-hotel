package com.hotel.domain.usecase;

import java.util.UUID;

public interface DeleteRoomCommand {

  void deleteRoom(UUID id);
}
