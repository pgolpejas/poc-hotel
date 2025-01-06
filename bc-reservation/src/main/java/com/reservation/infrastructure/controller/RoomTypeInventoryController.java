package com.reservation.infrastructure.controller;

import java.util.List;
import java.util.UUID;

import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.reservation.domain.model.RoomTypeInventory;
import com.reservation.domain.usecase.CreateRoomTypeInventoryCommand;
import com.reservation.domain.usecase.DeleteRoomTypeInventoryCommand;
import com.reservation.domain.usecase.GetRoomTypeInventoriesAuditQuery;
import com.reservation.domain.usecase.GetRoomTypeInventoriesQuery;
import com.reservation.domain.usecase.GetRoomTypeInventoryQuery;
import com.reservation.domain.usecase.UpdateRoomTypeInventoryCommand;
import com.reservation.infrastructure.controller.mapper.CriteriaMapper;
import com.reservation.infrastructure.controller.mapper.RoomTypeInventoryMapper;
import com.reservation.openapi.api.RoomTypeInventoryApi;
import com.reservation.openapi.model.AuditFiltersRequestDto;
import com.reservation.openapi.model.CriteriaRequestDto;
import com.reservation.openapi.model.Pagination;
import com.reservation.openapi.model.RoomTypeInventoryDto;
import com.reservation.openapi.model.RoomTypeInventoryListResponse;
import com.reservation.openapi.model.RoomTypeInventoryPaginationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class RoomTypeInventoryController implements RoomTypeInventoryApi {
  private final CreateRoomTypeInventoryCommand createRoomTypeInventoryCommand;

  private final UpdateRoomTypeInventoryCommand updateRoomTypeInventoryCommand;

  private final DeleteRoomTypeInventoryCommand deleteRoomTypeInventoryCommand;

  private final GetRoomTypeInventoryQuery getRoomTypeInventoryQuery;

  private final GetRoomTypeInventoriesQuery getRoomTypeInventoriesQuery;

  private final GetRoomTypeInventoriesAuditQuery getRoomTypeInventoriesAuditQuery;

  private final RoomTypeInventoryMapper roomTypeInventoryMapper;

  private final CriteriaMapper criteriaMapper;

  @Override
  public ResponseEntity<RoomTypeInventoryDto> createRoomTypeInventory(RoomTypeInventoryDto roomTypeInventoryDto) {
    this.createRoomTypeInventoryCommand
        .createRoomTypeInventory(this.roomTypeInventoryMapper.mapToAggregate(roomTypeInventoryDto));
    return ResponseEntity.status(HttpStatus.CREATED).body(roomTypeInventoryDto);
  }

  @Override
  public ResponseEntity<RoomTypeInventoryDto> deleteRoomTypeInventory(UUID id) {
    this.deleteRoomTypeInventoryCommand.deleteRoomTypeInventory(id);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<RoomTypeInventoryDto> getRoomTypeInventory(UUID id) {
    return ResponseEntity
        .ok(this.roomTypeInventoryMapper.mapToDTO(this.getRoomTypeInventoryQuery.getRoomTypeInventory(id)));
  }

  @Override
  public ResponseEntity<RoomTypeInventoryPaginationResponse> searchRoomTypeInventory(
      final CriteriaRequestDto criteriaRequestDto) {
    final PaginationResponse<RoomTypeInventory> search = this.getRoomTypeInventoriesQuery
        .getRoomTypeInventories(this.criteriaMapper.mapToCriteria(criteriaRequestDto));

    return ResponseEntity.ok(new RoomTypeInventoryPaginationResponse()
        .pagination(new Pagination().limit(search.pagination().limit()).page(search.pagination().page())
            .total(search.pagination().total()))
        .data(search.data().stream().map(this.roomTypeInventoryMapper::mapToDTO).toList()));
  }

  @Override
  public ResponseEntity<RoomTypeInventoryListResponse> searchAuditRoomTypeInventory(Integer limit,
      AuditFiltersRequestDto auditFilters) {
    final List<RoomTypeInventoryDto> search = this.getRoomTypeInventoriesAuditQuery
        .getRoomTypeInventoriesAudit(AuditFilters.builder().id(auditFilters.getId())
            .from(auditFilters.getFrom()).to(auditFilters.getTo()).build(), limit)
        .stream().map(this.roomTypeInventoryMapper::mapToDTO).toList();
    return ResponseEntity.ok(new RoomTypeInventoryListResponse().data(search));
  }

  @Override
  public ResponseEntity<RoomTypeInventoryDto> updateRoomTypeInventory(RoomTypeInventoryDto roomTypeInventoryDto) {
    this.updateRoomTypeInventoryCommand
        .updateRoomTypeInventory(this.roomTypeInventoryMapper.mapToAggregate(roomTypeInventoryDto));
    return ResponseEntity.status(HttpStatus.OK).body(roomTypeInventoryDto);
  }

}
