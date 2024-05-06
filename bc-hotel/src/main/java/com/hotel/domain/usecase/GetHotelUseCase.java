package com.hotel.domain.usecase;

import com.hotel.domain.model.Hotel;

import java.util.UUID;

public interface GetHotelUseCase {

    Hotel getHotel(UUID id);
}
