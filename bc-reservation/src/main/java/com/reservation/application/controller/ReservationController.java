package com.reservation.application.controller;

import com.reservation.application.dto.ReservationDto;
import com.reservation.application.mapper.ReservationMapper;
import com.reservation.domain.usecase.CreateReservationUseCase;
import com.reservation.domain.usecase.GetReservationUseCase;
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

    private final CreateReservationUseCase createReservationUseCase;
    private final GetReservationUseCase getReservationUseCase;
    private final ReservationMapper reservationMapper;

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
