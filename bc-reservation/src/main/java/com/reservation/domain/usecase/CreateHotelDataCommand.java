package com.reservation.domain.usecase;

import com.reservation.domain.model.Hotel;
import org.springframework.validation.annotation.Validated;

@Validated
public interface CreateHotelDataCommand {

  void createHotelData(Hotel hotel);
}
