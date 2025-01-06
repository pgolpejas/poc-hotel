package com.hotel.application.usecase;

import java.util.Optional;
import java.util.UUID;

import com.hotel.domain.exception.HotelNotFoundException;
import com.hotel.domain.model.Hotel;
import com.hotel.domain.repository.HotelRepository;
import com.hotel.domain.usecase.DeleteHotelCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeleteHotelCommandHandler implements DeleteHotelCommand {

  private final HotelRepository hotelRepository;

  @Transactional
  @Override
  public void deleteHotel(final UUID id) {
    final Optional<Hotel> optionalHotel = this.hotelRepository.findById(id);

    if (optionalHotel.isEmpty()) {
      throw new HotelNotFoundException("Hotel with id %s not found", id.toString());
    } else {
      final Hotel hotel = optionalHotel.get();
      hotel.delete();
      this.hotelRepository.delete(hotel);
    }
  }
}
