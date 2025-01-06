package com.reservation.domain.usecase;

import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.PaginationResponse;
import com.reservation.domain.dto.AggregatedReservationCriteria;
import com.reservation.domain.model.AggregatedReservation;
import com.reservation.domain.model.Reservation;

public interface GetReservationsQuery {

  PaginationResponse<Reservation> getReservations(Criteria searchDto);

  PaginationResponse<AggregatedReservation> getAggregatedReservations(
      AggregatedReservationCriteria aggregatedReservationCriteria, boolean searchByMongo);
}
