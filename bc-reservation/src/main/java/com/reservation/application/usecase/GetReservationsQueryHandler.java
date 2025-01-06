package com.reservation.application.usecase;

import java.util.Set;

import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.domain.exception.CustomValidationException;
import com.hotel.core.domain.utils.ValidationUtils;
import com.reservation.domain.dto.AggregatedReservationCriteria;
import com.reservation.domain.model.AggregatedReservation;
import com.reservation.domain.model.Reservation;
import com.reservation.domain.repository.AggregatedReservationRepository;
import com.reservation.domain.repository.ReservationRepository;
import com.reservation.domain.usecase.GetReservationsQuery;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetReservationsQueryHandler implements GetReservationsQuery {

  private final ReservationRepository reservationRepository;

  private final AggregatedReservationRepository aggregatedReservationRepository;

  private final Validator validator;

  @Override
  public PaginationResponse<Reservation> getReservations(final Criteria searchDto) {
    return this.reservationRepository.search(searchDto);
  }

  @Override
  public PaginationResponse<AggregatedReservation> getAggregatedReservations(
      final AggregatedReservationCriteria aggregatedReservationCriteria, boolean searchByMongo) {

    final Set<ConstraintViolation<AggregatedReservationCriteria>> violations = this.validator.validate(aggregatedReservationCriteria);
    if (!violations.isEmpty()) {
      throw new CustomValidationException(
          ValidationUtils.parseViolationsToString(violations, aggregatedReservationCriteria.getClass().getSimpleName()));
    }

    if (searchByMongo) {
      return this.aggregatedReservationRepository.searchByMongo(aggregatedReservationCriteria);
    }

    return this.aggregatedReservationRepository.searchBySelection(aggregatedReservationCriteria);
  }

}
