package com.hotel.infrastructure.controller;

import java.util.List;
import java.util.UUID;

import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.domain.model.Hotel;
import com.hotel.domain.usecase.CreateHotelCommand;
import com.hotel.domain.usecase.DeleteHotelCommand;
import com.hotel.domain.usecase.GetHotelQuery;
import com.hotel.domain.usecase.GetHotelsAuditQuery;
import com.hotel.domain.usecase.GetHotelsQuery;
import com.hotel.domain.usecase.UpdateHotelCommand;
import com.hotel.infrastructure.controller.mapper.CriteriaMapper;
import com.hotel.infrastructure.controller.mapper.HotelMapper;
import com.hotel.openapi.api.HotelApi;
import com.hotel.openapi.model.AuditFiltersRequestDto;
import com.hotel.openapi.model.CriteriaRequestDto;
import com.hotel.openapi.model.HotelDto;
import com.hotel.openapi.model.HotelListResponse;
import com.hotel.openapi.model.HotelPaginationResponse;
import com.hotel.openapi.model.Pagination;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HotelController implements HotelApi {
  private final CreateHotelCommand createHotelCommand;

  private final UpdateHotelCommand updateHotelCommand;

  private final DeleteHotelCommand deleteHotelCommand;

  private final GetHotelQuery getHotelQuery;

  private final GetHotelsQuery getHotelsQuery;

  private final GetHotelsAuditQuery getHotelsAuditQuery;

  private final HotelMapper hotelMapper;

  private final CriteriaMapper criteriaMapper;

  @Override
  public ResponseEntity<HotelDto> createHotel(HotelDto hotelDto) {
    this.createHotelCommand.createHotel(this.hotelMapper.mapToAggregate(hotelDto));
    return ResponseEntity.status(HttpStatus.CREATED).body(hotelDto);
  }

  @Override
  public ResponseEntity<HotelDto> deleteHotel(UUID id) {
    this.deleteHotelCommand.deleteHotel(id);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<HotelDto> getHotel(UUID id) {
    return ResponseEntity.ok(this.hotelMapper.mapToDTO(this.getHotelQuery.getHotel(id)));
  }

  @Override
  public ResponseEntity<HotelPaginationResponse> searchHotel(final CriteriaRequestDto criteriaRequestDto) {
    final PaginationResponse<Hotel> search = this.getHotelsQuery
        .getHotels(this.criteriaMapper.mapToAggregate(criteriaRequestDto));

    return ResponseEntity.ok(new HotelPaginationResponse()
        .pagination(new Pagination()
            .limit(search.pagination().limit())
            .page(search.pagination().page())
            .total(search.pagination().total()))
        .data(search.data().stream().map(this.hotelMapper::mapToDTO).toList()));
  }

  @Override
  public ResponseEntity<HotelListResponse> searchAuditHotel(final Integer limit,
      AuditFiltersRequestDto auditFilters) {
    final List<HotelDto> search = this.getHotelsAuditQuery.getHotelsAudit(AuditFilters.builder()
        .id(auditFilters.getId()).from(auditFilters.getFrom()).to(auditFilters.getTo()).build(), limit).stream()
        .map(this.hotelMapper::mapToDTO).toList();
    return ResponseEntity.ok(new HotelListResponse().data(search));
  }

  @Transactional
  @Override
  public ResponseEntity<HotelDto> updateHotel(HotelDto hotelDto) {
    this.updateHotelCommand.updateHotel(this.hotelMapper.mapToAggregate(hotelDto));
    return ResponseEntity.status(HttpStatus.OK).body(hotelDto);
  }

}
