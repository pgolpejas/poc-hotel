package com.reservation.application.controller;

import com.hotel.core.application.dto.CriteriaDto;
import com.hotel.core.application.mapper.CriteriaMapper;
import com.hotel.core.domain.dto.ListResponse;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.reservation.application.dto.ReservationDto;
import com.reservation.application.mapper.ReservationMapper;
import com.reservation.domain.model.Reservation;
import com.reservation.domain.usecase.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Hotel Reservation controller", description = "Hotel reservation API")
@RequestMapping(value = ReservationController.MAPPING)
@Slf4j
@RequiredArgsConstructor
public class ReservationController {

    public static final String DELIMITER_PATH = "/";
    public static final String MAPPING = DELIMITER_PATH + "v1/hotel-reservation";
    public static final String FIND_BY_ID_PATH = DELIMITER_PATH + "{id}";
    public static final String DELETE_PATH = DELIMITER_PATH + "{id}";
    public static final String SEARCH_PATH = DELIMITER_PATH + "search";
    public static final String SEARCH_AUDIT_PATH = DELIMITER_PATH + "search-audit/{limit}";

    private final CreateReservationUseCase createReservationUseCase;
    private final UpdateReservationUseCase updateReservationUseCase;
    private final DeleteReservationUseCase deleteReservationUseCase;
    private final GetReservationUseCase getReservationUseCase;
    private final GetReservationsUseCase getReservationsUseCase;
    private final GetReservationsAuditUseCase getReservationsAuditUseCase;
    private final ReservationMapper reservationMapper;
    private final CriteriaMapper criteriaMapper;

    @Operation(summary = "Search by filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found results",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PaginationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content(mediaType = "application/problem+json"))})
    @PostMapping(value = SEARCH_PATH)
    public ResponseEntity<PaginationResponse<ReservationDto>> search(@Valid @RequestBody CriteriaDto searchDto) {
        PaginationResponse<Reservation> search = getReservationsUseCase.getReservations(this.criteriaMapper.mapToAggregate(searchDto));

        return ResponseEntity.ok(PaginationResponse.<ReservationDto>builder()
                .pagination(search.pagination())
                .data(search.data().stream().map(this.reservationMapper::mapToDTO).toList())
                .build());
    }

    @Operation(summary = "Search audit by filters")
    @Parameter(description = "number of items to return", required = true, name = "limit", example = "10")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found audit results",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ListResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content(mediaType = "application/problem+json"))})
    @PostMapping(value = SEARCH_AUDIT_PATH)
    public ResponseEntity<ListResponse<ReservationDto>> searchAudit(@Valid @RequestBody AuditFilters filters, @PathVariable(name = "limit") int limit) {
        List<ReservationDto> search = getReservationsAuditUseCase.getReservationsAudit(filters, limit).stream()
                .map(this.reservationMapper::mapToDTO).toList();
        return ResponseEntity.ok(ListResponse.<ReservationDto>builder().data(search).build());
    }

    @Operation(summary = "Search item by id")
    @Parameter(description = "item id", required = true, name = "id", example = "d1a97f69-7fa0-4301-b498-128d78860828")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found item",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReservationDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content(mediaType = "application/problem+json"))})
    @GetMapping(value = FIND_BY_ID_PATH)
    public ResponseEntity<ReservationDto> getReservation(@PathVariable("id") UUID id) {
        return ResponseEntity
                .ok(this.reservationMapper.mapToDTO(this.getReservationUseCase.getReservation(id)));
    }

    @Operation(summary = "Create item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Created item",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReservationDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(mediaType = "application/problem+json"))}
    )
    @PostMapping
    public ResponseEntity<ReservationDto> createReservation(@Valid @RequestBody final ReservationDto reservationDTO) {
        createReservationUseCase.createReservation(this.reservationMapper.mapToAggregate(reservationDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationDTO);
    }

    @Operation(summary = "Update item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Updated item",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReservationDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "application/problem+json"))}
    )
    @PutMapping
    public ResponseEntity<ReservationDto> updateReservation(@Valid @RequestBody final ReservationDto reservationDTO) {
        updateReservationUseCase.updateReservation(this.reservationMapper.mapToAggregate(reservationDTO));
        return ResponseEntity.status(HttpStatus.OK).body(reservationDTO);

    }

    @Operation(summary = "Delete item")
    @Parameter(description = "item id", required = true, name = "id", example = "d1a97f69-7fa0-4301-b498-128d78860828")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Deleted item",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReservationDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "application/problem+json"))}
    )
    @DeleteMapping(value = DELETE_PATH)
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") UUID id) {
        this.deleteReservationUseCase.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

}
