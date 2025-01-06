package com.hotel.domain.usecase;

import com.hotel.domain.model.Hotel;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

@Validated
public interface UpdateHotelCommand {

  void updateHotel(@Valid Hotel hotel);
}
