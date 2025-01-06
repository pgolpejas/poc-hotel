package com.reservation.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.reservation.domain.model.Hotel;

public interface HotelRepository {

  void save(Hotel hotel);

  Optional<Hotel> findById(UUID id);
}
