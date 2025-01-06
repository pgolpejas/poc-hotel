package com.hotel.application.usecase;

import java.util.Optional;

import com.hotel.domain.exception.HotelNotFoundException;
import com.hotel.domain.model.Hotel;
import com.hotel.domain.repository.HotelRepository;
import com.hotel.domain.usecase.UpdateHotelCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UpdateHotelCommandHandler implements UpdateHotelCommand {

  private final HotelRepository hotelRepository;

  @Transactional
  @Override
  public void updateHotel(final Hotel hotel) {

    final Optional<Hotel> optionalHotel = this.hotelRepository.findById(hotel.id());

    if (optionalHotel.isEmpty()) {
      throw new HotelNotFoundException("Hotel with id %s not found", hotel.id().toString());
    } else {
      final Hotel aggregate = optionalHotel.get();
      aggregate.update(hotel);
      this.hotelRepository.save(aggregate);
    }
  }
}
