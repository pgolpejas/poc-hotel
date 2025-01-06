package com.reservation.infrastructure.controller;

import com.hotel.core.domain.dto.PaginationResponse;
import com.reservation.domain.model.AggregatedReservation;
import com.reservation.domain.usecase.GetReservationsQuery;
import com.reservation.infrastructure.controller.mapper.CriteriaMapper;
import com.reservation.infrastructure.controller.mapper.ReservationMapper;
import com.reservation.openapi.api.ReservationInformationalApi;
import com.reservation.openapi.model.AggregatedReservationCriteriaDto;
import com.reservation.openapi.model.AggregatedReservationCriteriaDto.DatabaseEnum;
import com.reservation.openapi.model.AggregatedReservationPaginationResponse;
import com.reservation.openapi.model.Pagination;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ReservationInformationalController implements ReservationInformationalApi {

  private final GetReservationsQuery getReservationsQuery;

  private final ReservationMapper reservationMapper;

  private final CriteriaMapper criteriaMapper;

  @Override
  public ResponseEntity<AggregatedReservationPaginationResponse> searchInformationalReservation(
      final AggregatedReservationCriteriaDto criteriaRequestDto) {
    final PaginationResponse<AggregatedReservation> search = this.getReservationsQuery
        .getAggregatedReservations(this.criteriaMapper.mapToAggregateCriteria(criteriaRequestDto),
            criteriaRequestDto.getDatabase().equals(DatabaseEnum.MONGO));

    return ResponseEntity.ok(new AggregatedReservationPaginationResponse()
        .pagination(new Pagination()
            .limit(search.pagination().limit())
            .page(search.pagination().page())
            .total(search.pagination().total()))
        .data(search.data().stream()
            .map(AggregatedReservation::getReservation)
            .map(this.reservationMapper::mapToAggregateResponseDTO)
            .toList()));
  }

}
