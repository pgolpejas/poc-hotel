package com.hotel.domain.usecase;

import java.util.UUID;

import com.hotel.domain.model.Hotel;

public interface GetHotelQuery {

  Hotel getHotel(UUID id);
}
