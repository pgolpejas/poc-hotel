package com.hotel.application.usecase;

import java.util.UUID;

import com.hotel.domain.exception.HotelNotFoundException;
import com.hotel.domain.model.Hotel;
import com.hotel.domain.repository.HotelRepository;
import com.hotel.domain.usecase.GetHotelQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetHotelQueryHandler implements GetHotelQuery {

  private final HotelRepository hotelRepository;

  @Override
  public Hotel getHotel(final UUID id) {
    return this.hotelRepository.findById(id)
        .orElseThrow(() -> new HotelNotFoundException("Hotel with id %s not found", id));
  }
}
