package com.reservation.application.controller;

import com.reservation.application.dto.CriteriaDto;
import com.reservation.application.dto.ReservationDto;
import com.reservation.application.mapper.CriteriaMapper;
import com.reservation.application.mapper.ReservationMapper;
import com.reservation.domain.model.Reservations;
import com.reservation.domain.usecase.CreateReservationUseCase;
import com.reservation.domain.usecase.GetReservationUseCase;
import com.reservation.domain.usecase.GetReservationsUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(name = "Hotel Reservation controller", description = "Hotel reservation API")
@RequestMapping(value = ReservationController.MAPPING)
@RequiredArgsConstructor
public class ReservationController {

    public static final String DELIMITER_PATH = "/";
    public static final String MAPPING = DELIMITER_PATH + "v1/hotel-reservation";
    public static final String FIND_BY_ID_PATH = DELIMITER_PATH + "{id}";
    public static final String SEARCH_PATH = DELIMITER_PATH + "search";

    private final CreateReservationUseCase createReservationUseCase;
    private final GetReservationUseCase getReservationUseCase;
    private final GetReservationsUseCase getReservationsUseCase;
    private final ReservationMapper reservationMapper;
    private final CriteriaMapper criteriaMapper;

    @PostMapping(value = SEARCH_PATH)
    public ResponseEntity<Reservations> search(@Valid @RequestBody CriteriaDto searchDto) {
        Reservations search = getReservationsUseCase.getReservations(this.criteriaMapper.mapToAggregate(searchDto));
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

}
