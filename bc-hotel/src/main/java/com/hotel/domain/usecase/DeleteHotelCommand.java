package com.hotel.domain.usecase;

import java.util.UUID;

public interface DeleteHotelCommand {

  void deleteHotel(UUID id);
}
