package com.reservation.domain.usecase;

import com.reservation.domain.model.AggregatedReservation.Reservation;
import org.springframework.validation.annotation.Validated;

@Validated
public interface CreateReservationDataCommand {

  void createReservationData(Reservation reservation);
}
