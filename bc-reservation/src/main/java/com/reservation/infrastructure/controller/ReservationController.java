package com.reservation.infrastructure.controller;

import java.util.List;
import java.util.UUID;

import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.reservation.domain.model.Reservation;
import com.reservation.domain.usecase.CreateReservationCommand;
import com.reservation.domain.usecase.DeleteReservationCommand;
import com.reservation.domain.usecase.GetReservationQuery;
import com.reservation.domain.usecase.GetReservationsAuditQuery;
import com.reservation.domain.usecase.GetReservationsQuery;
import com.reservation.domain.usecase.UpdateReservationCommand;
import com.reservation.infrastructure.controller.mapper.CriteriaMapper;
import com.reservation.infrastructure.controller.mapper.ReservationMapper;
import com.reservation.openapi.api.ReservationApi;
import com.reservation.openapi.model.AuditFiltersRequestDto;
import com.reservation.openapi.model.CriteriaRequestDto;
import com.reservation.openapi.model.Pagination;
import com.reservation.openapi.model.ReservationDto;
import com.reservation.openapi.model.ReservationListResponse;
import com.reservation.openapi.model.ReservationPaginationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ReservationController implements ReservationApi {
  private final CreateReservationCommand createReservationCommand;

  private final UpdateReservationCommand updateReservationCommand;

  private final DeleteReservationCommand deleteReservationCommand;

  private final GetReservationQuery getReservationQuery;

  private final GetReservationsQuery getReservationsQuery;

  private final GetReservationsAuditQuery getReservationsAuditQuery;

  private final ReservationMapper reservationMapper;

  private final CriteriaMapper criteriaMapper;

  @Override
  public ResponseEntity<ReservationDto> createReservation(ReservationDto reservationDto) {
    this.createReservationCommand.createReservation(this.reservationMapper.mapToAggregate(reservationDto));
    return ResponseEntity.status(HttpStatus.CREATED).body(reservationDto);
  }

  @Override
  public ResponseEntity<ReservationDto> deleteReservation(UUID id) {
    this.deleteReservationCommand.deleteReservation(id);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<ReservationDto> getReservation(UUID id) {
    return ResponseEntity.ok(this.reservationMapper.mapToDTO(this.getReservationQuery.getReservation(id)));
  }

  @Override
  public ResponseEntity<ReservationPaginationResponse> searchReservation(
      final CriteriaRequestDto criteriaRequestDto) {
    final PaginationResponse<Reservation> search = this.getReservationsQuery
        .getReservations(this.criteriaMapper.mapToCriteria(criteriaRequestDto));

    return ResponseEntity.ok(new ReservationPaginationResponse()
        .pagination(new Pagination()
            .limit(search.pagination().limit())
            .page(search.pagination().page())
            .total(search.pagination().total()))
        .data(search.data().stream()
            .map(this.reservationMapper::mapToDTO)
            .toList()));
  }

  @Override
  public ResponseEntity<ReservationListResponse> searchAuditReservation(Integer limit,
      AuditFiltersRequestDto auditFilters) {
    final List<ReservationDto> search = this.getReservationsAuditQuery
        .getReservationsAudit(AuditFilters.builder()
            .id(auditFilters.getId())
            .from(auditFilters.getFrom())
            .to(auditFilters.getTo())
            .build(), limit)
        .stream().map(this.reservationMapper::mapToDTO).toList();
    return ResponseEntity.ok(new ReservationListResponse().data(search));
  }

  @Transactional
  @Override
  public ResponseEntity<ReservationDto> updateReservation(ReservationDto reservationDto) {
    this.updateReservationCommand.updateReservation(this.reservationMapper.mapToAggregate(reservationDto));
    return ResponseEntity.status(HttpStatus.OK).body(reservationDto);
  }

}
