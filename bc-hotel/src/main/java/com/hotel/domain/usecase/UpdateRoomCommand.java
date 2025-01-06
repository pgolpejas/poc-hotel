package com.hotel.domain.usecase;

import com.hotel.domain.model.Room;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

@Validated
public interface UpdateRoomCommand {

  void updateRoom(@Valid Room room);
}
