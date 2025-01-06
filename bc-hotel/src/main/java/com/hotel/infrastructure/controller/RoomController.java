package com.hotel.infrastructure.controller;

import java.util.List;
import java.util.UUID;

import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.domain.model.Room;
import com.hotel.domain.usecase.CreateRoomCommand;
import com.hotel.domain.usecase.DeleteRoomCommand;
import com.hotel.domain.usecase.GetRoomQuery;
import com.hotel.domain.usecase.GetRoomsAuditQuery;
import com.hotel.domain.usecase.GetRoomsQuery;
import com.hotel.domain.usecase.UpdateRoomCommand;
import com.hotel.infrastructure.controller.mapper.CriteriaMapper;
import com.hotel.infrastructure.controller.mapper.RoomMapper;
import com.hotel.openapi.api.RoomApi;
import com.hotel.openapi.model.AuditFiltersRequestDto;
import com.hotel.openapi.model.CriteriaRequestDto;
import com.hotel.openapi.model.Pagination;
import com.hotel.openapi.model.RoomDto;
import com.hotel.openapi.model.RoomListResponse;
import com.hotel.openapi.model.RoomPaginationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class RoomController implements RoomApi {
  private final CreateRoomCommand createRoomCommand;

  private final UpdateRoomCommand updateRoomCommand;

  private final DeleteRoomCommand deleteRoomCommand;

  private final GetRoomQuery getRoomQuery;

  private final GetRoomsQuery getRoomsQuery;

  private final GetRoomsAuditQuery getRoomsAuditQuery;

  private final RoomMapper roomMapper;

  private final CriteriaMapper criteriaMapper;

  @Override
  public ResponseEntity<RoomDto> createRoom(RoomDto roomDto) {
    this.createRoomCommand.createRoom(this.roomMapper.mapToAggregate(roomDto));
    return ResponseEntity.status(HttpStatus.CREATED).body(roomDto);
  }

  @Override
  public ResponseEntity<RoomDto> deleteRoom(UUID id) {
    this.deleteRoomCommand.deleteRoom(id);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<RoomDto> getRoom(UUID id) {
    return ResponseEntity.ok(this.roomMapper.mapToDTO(this.getRoomQuery.getRoom(id)));
  }

  @Override
  public ResponseEntity<RoomPaginationResponse> searchRoom(final CriteriaRequestDto criteriaRequestDto) {
    final PaginationResponse<Room> search = this.getRoomsQuery.getRooms(this.criteriaMapper.mapToAggregate(criteriaRequestDto));

    return ResponseEntity.ok(new RoomPaginationResponse()
        .pagination(new Pagination()
            .limit(search.pagination().limit())
            .page(search.pagination().page())
            .total(search.pagination().total()))
        .data(search.data().stream().map(this.roomMapper::mapToDTO).toList()));
  }

  @Override
  public ResponseEntity<RoomListResponse> searchAuditRoom(Integer limit, AuditFiltersRequestDto auditFilters) {
    final List<RoomDto> search = this.getRoomsAuditQuery.getRoomsAudit(AuditFilters.builder()
        .id(auditFilters.getId())
        .from(auditFilters.getFrom())
        .to(auditFilters.getTo())
        .build(), limit).stream()
        .map(this.roomMapper::mapToDTO).toList();
    return ResponseEntity.ok(new RoomListResponse().data(search));
  }

  @Override
  public ResponseEntity<RoomDto> updateRoom(RoomDto roomDto) {
    this.updateRoomCommand.updateRoom(this.roomMapper.mapToAggregate(roomDto));
    return ResponseEntity.status(HttpStatus.OK).body(roomDto);
  }

}
