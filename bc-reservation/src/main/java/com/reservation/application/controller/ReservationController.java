package com.reservation.application.controller;

import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.reservation.application.mapper.CriteriaMapper;
import com.reservation.application.mapper.ReservationMapper;
import com.reservation.domain.model.Reservation;
import com.reservation.domain.usecase.*;
import com.reservation.openapi.api.ReservationApi;
import com.reservation.openapi.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ReservationController implements ReservationApi {
    private final CreateReservationUseCase createReservationUseCase;
    private final UpdateReservationUseCase updateReservationUseCase;
    private final DeleteReservationUseCase deleteReservationUseCase;
    private final GetReservationUseCase getReservationUseCase;
    private final GetReservationsUseCase getReservationsUseCase;
    private final GetReservationsAuditUseCase getReservationsAuditUseCase;
    private final ReservationMapper reservationMapper;
    private final CriteriaMapper criteriaMapper;

    @Override
    public ResponseEntity<ReservationDto> createReservation(ReservationDto reservationDto) {
        createReservationUseCase.createReservation(this.reservationMapper.mapToAggregate(reservationDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationDto);
    }

    @Override
    public ResponseEntity<ReservationDto> deleteReservation(UUID id) {
        this.deleteReservationUseCase.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ReservationDto> getReservation(UUID id) {
        return ResponseEntity.ok(this.reservationMapper.mapToDTO(this.getReservationUseCase.getReservation(id)));
    }

    @Override
    public ResponseEntity<ReservationPaginationResponse> searchReservation(final CriteriaRequestDto criteriaRequestDto) {
        PaginationResponse<Reservation> search = getReservationsUseCase.getReservations(this.criteriaMapper.mapToAggregate(criteriaRequestDto));

        return ResponseEntity.ok(new ReservationPaginationResponse()
                .pagination(new Pagination()
                        .limit(search.pagination().limit())
                        .page(search.pagination().page())
                        .total(search.pagination().total())
                )
                .data(search.data().stream().map(this.reservationMapper::mapToDTO).toList())
        );
    }

    @Override
    public ResponseEntity<ReservationListResponse> searchAuditReservation(Integer limit, AuditFiltersRequestDto auditFilters) {
        List<ReservationDto> search = getReservationsAuditUseCase.getReservationsAudit(AuditFilters.builder()
                        .id(auditFilters.getId())
                        .from(auditFilters.getFrom())
                        .to(auditFilters.getTo())
                        .build(), limit).stream()
                .map(this.reservationMapper::mapToDTO).toList();
        return ResponseEntity.ok(new ReservationListResponse().data(search));
    }

    @Transactional
    @Override
    public ResponseEntity<ReservationDto> updateReservation(ReservationDto reservationDto) {
        updateReservationUseCase.updateReservation(this.reservationMapper.mapToAggregate(reservationDto));
        return ResponseEntity.status(HttpStatus.OK).body(reservationDto);
    }

}
