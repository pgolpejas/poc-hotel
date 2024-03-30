package com.reservation.application.controller;

import com.hotel.core.application.dto.CriteriaDto;
import com.hotel.core.application.mapper.CriteriaMapper;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.reservation.application.dto.ReservationDto;
import com.reservation.application.mapper.ReservationMapper;
import com.reservation.domain.model.Reservation;
import com.reservation.domain.usecase.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Hotel Reservation controller", description = "Hotel reservation API")
@RequestMapping(value = ReservationController.MAPPING)
@RequiredArgsConstructor
public class ReservationController {

    public static final String DELIMITER_PATH = "/";
    public static final String MAPPING = DELIMITER_PATH + "v1/hotel-reservation";
    public static final String FIND_BY_ID_PATH = DELIMITER_PATH + "{id}";
    public static final String DELETE_PATH = DELIMITER_PATH + "{id}";
    public static final String SEARCH_PATH = DELIMITER_PATH + "search";
    public static final String SEARCH_AUDIT_PATH = DELIMITER_PATH + "search-audit/{limit}";

    private final CreateReservationUseCase createReservationUseCase;
    private final DeleteReservationUseCase deleteReservationUseCase;
    private final GetReservationUseCase getReservationUseCase;
    private final GetReservationsUseCase getReservationsUseCase;
    private final GetReservationsAuditUseCase getReservationsAuditUseCase;
    private final ReservationMapper reservationMapper;
    private final CriteriaMapper criteriaMapper;

    @PostMapping(value = SEARCH_PATH)
    public ResponseEntity<PaginationResponse<ReservationDto>> search(@Valid @RequestBody CriteriaDto searchDto) {
        PaginationResponse<Reservation> search = getReservationsUseCase.getReservations(this.criteriaMapper.mapToAggregate(searchDto));

        return ResponseEntity.ok(PaginationResponse.<ReservationDto>builder()
                .pagination(search.pagination())
                .data(search.data().stream().map(this.reservationMapper::mapToDTO).toList())
                .build());
    }

    @PostMapping(value = SEARCH_AUDIT_PATH)
    public ResponseEntity<List<ReservationDto>> searchAudit(@Valid @RequestBody AuditFilters filters, @PathVariable("limit") int limit) {
        List<ReservationDto> search = getReservationsAuditUseCase.getReservationsAudit(filters, limit).stream()
                .map(this.reservationMapper::mapToDTO).toList();
        return ResponseEntity.ok(search);
    }

    @GetMapping(value = FIND_BY_ID_PATH)
    public ResponseEntity<ReservationDto> getReservation(@PathVariable("id") UUID id) {
        return ResponseEntity
                .ok(this.reservationMapper.mapToDTO(this.getReservationUseCase.getReservation(id)));
    }

    @PostMapping
    public ResponseEntity<ReservationDto> createReservation(@Valid @RequestBody final ReservationDto reservationDTO) {
        createReservationUseCase.createReservation(this.reservationMapper.mapToAggregate(reservationDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationDTO);
    }

    @DeleteMapping(value = DELETE_PATH)
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") UUID id) {
        this.deleteReservationUseCase.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
