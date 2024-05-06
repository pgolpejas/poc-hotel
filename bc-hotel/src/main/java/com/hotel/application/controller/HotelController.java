package com.hotel.application.controller;

import com.hotel.application.mapper.CriteriaMapper;
import com.hotel.application.mapper.HotelMapper;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.domain.model.Hotel;
import com.hotel.domain.usecase.*;
import com.hotel.openapi.api.HotelApi;
import com.hotel.openapi.model.*;
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
public class HotelController implements HotelApi {
    private final CreateHotelUseCase createHotelUseCase;
    private final UpdateHotelUseCase updateHotelUseCase;
    private final DeleteHotelUseCase deleteHotelUseCase;
    private final GetHotelUseCase getHotelUseCase;
    private final GetHotelsUseCase getHotelsUseCase;
    private final GetHotelsAuditUseCase getHotelsAuditUseCase;
    private final HotelMapper hotelMapper;
    private final CriteriaMapper criteriaMapper;

    @Override
    public ResponseEntity<HotelDto> createHotel(HotelDto hotelDto) {
        createHotelUseCase.createHotel(this.hotelMapper.mapToAggregate(hotelDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelDto);
    }

    @Override
    public ResponseEntity<HotelDto> deleteHotel(UUID id) {
        this.deleteHotelUseCase.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<HotelDto> getHotel(UUID id) {
        return ResponseEntity.ok(this.hotelMapper.mapToDTO(this.getHotelUseCase.getHotel(id)));
    }

    @Override
    public ResponseEntity<HotelPaginationResponse> searchHotel(final CriteriaRequestDto criteriaRequestDto) {
        PaginationResponse<Hotel> search = getHotelsUseCase.getHotels(this.criteriaMapper.mapToAggregate(criteriaRequestDto));

        return ResponseEntity.ok(new HotelPaginationResponse()
                .pagination(new Pagination()
                        .limit(search.pagination().limit())
                        .page(search.pagination().page())
                        .total(search.pagination().total())
                )
                .data(search.data().stream().map(this.hotelMapper::mapToDTO).toList())
        );
    }

    @Override
    public ResponseEntity<HotelListResponse> searchAuditHotel(Integer limit, AuditFiltersRequestDto auditFilters) {
        List<HotelDto> search = getHotelsAuditUseCase.getHotelsAudit(AuditFilters.builder()
                        .id(auditFilters.getId())
                        .from(auditFilters.getFrom())
                        .to(auditFilters.getTo())
                        .build(), limit).stream()
                .map(this.hotelMapper::mapToDTO).toList();
        return ResponseEntity.ok(new HotelListResponse().data(search));
    }

    @Transactional
    @Override
    public ResponseEntity<HotelDto> updateHotel(HotelDto hotelDto) {
        updateHotelUseCase.updateHotel(this.hotelMapper.mapToAggregate(hotelDto));
        return ResponseEntity.status(HttpStatus.OK).body(hotelDto);
    }

}
