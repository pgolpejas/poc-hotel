package com.reservation.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.hotel.core.domain.dto.PaginationResponse;
import com.reservation.domain.dto.AggregatedReservationCriteria;
import com.reservation.domain.model.AggregatedReservation;

public interface AggregatedReservationRepository {
  boolean existsById(UUID id);

  Optional<AggregatedReservation> findById(UUID id);

  void update(AggregatedReservation reservation);

  void save(AggregatedReservation reservation);

  void deleteById(UUID id);

  PaginationResponse<AggregatedReservation> searchBySelection(AggregatedReservationCriteria criteria);

  // Mongo methods
  PaginationResponse<AggregatedReservation> searchByMongo(AggregatedReservationCriteria criteria);

  void saveMongo(AggregatedReservation aggregatedReservation);
}
