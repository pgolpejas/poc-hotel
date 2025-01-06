package com.hotel.application.usecase;

import com.hotel.domain.exception.HotelConflictException;
import com.hotel.domain.model.Hotel;
import com.hotel.domain.repository.HotelRepository;
import com.hotel.domain.usecase.CreateHotelCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Component
@RequiredArgsConstructor
@Validated
public class CreateHotelCommandHandler implements CreateHotelCommand {

  private final HotelRepository hotelRepository;

  @Transactional
  @Override
  public void createHotel(@Valid final Hotel hotel) {

    if (this.hotelRepository.existsById(hotel.id())) {
      throw new HotelConflictException("Hotel with id %s already exists", hotel.id().toString());
    }
    if (this.hotelRepository.existsByUK(hotel.name())) {
      throw new HotelConflictException("Hotel with name %s already exists", hotel.name());
    }

    final Hotel newHotel = Hotel.create(hotel);

    this.hotelRepository.save(newHotel);
  }

}
