package com.hotel.application.controller;

import com.hotel.application.mapper.CriteriaMapper;
import com.hotel.application.mapper.RoomMapper;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.domain.model.Room;
import com.hotel.domain.usecase.*;
import com.hotel.openapi.api.RoomApi;
import com.hotel.openapi.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@Slf4j
@RequiredArgsConstructor
public class RoomController implements RoomApi {
    private final CreateRoomUseCase createRoomUseCase;
    private final UpdateRoomUseCase updateRoomUseCase;
    private final DeleteRoomUseCase deleteRoomUseCase;
    private final GetRoomUseCase getRoomUseCase;
    private final GetRoomsUseCase getRoomsUseCase;
    private final GetRoomsAuditUseCase getRoomsAuditUseCase;
    private final RoomMapper roomMapper;
    private final CriteriaMapper criteriaMapper;

    @Override
    public ResponseEntity<RoomDto> createRoom(RoomDto roomDto) {
        createRoomUseCase.createRoom(this.roomMapper.mapToAggregate(roomDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(roomDto);
    }

    @Override
    public ResponseEntity<RoomDto> deleteRoom(UUID id) {
        this.deleteRoomUseCase.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<RoomDto> getRoom(UUID id) {
        return ResponseEntity.ok(this.roomMapper.mapToDTO(this.getRoomUseCase.getRoom(id)));
    }

    @Override
    public ResponseEntity<RoomPaginationResponse> searchRoom(final CriteriaRequestDto criteriaRequestDto) {
        PaginationResponse<Room> search = getRoomsUseCase.getRooms(this.criteriaMapper.mapToAggregate(criteriaRequestDto));

        return ResponseEntity.ok(new RoomPaginationResponse()
                .pagination(new Pagination()
                        .limit(search.pagination().limit())
                        .page(search.pagination().page())
                        .total(search.pagination().total())
                )
                .data(search.data().stream().map(this.roomMapper::mapToDTO).toList())
        );
    }

    @Override
    public ResponseEntity<RoomListResponse> searchAuditRoom(Integer limit, AuditFiltersRequestDto auditFilters) {
        List<RoomDto> search = getRoomsAuditUseCase.getRoomsAudit(AuditFilters.builder()
                        .id(auditFilters.getId())
                        .from(auditFilters.getFrom())
                        .to(auditFilters.getTo())
                        .build(), limit).stream()
                .map(this.roomMapper::mapToDTO).toList();
        return ResponseEntity.ok(new RoomListResponse().data(search));
    }

    @Override
    public ResponseEntity<RoomDto> updateRoom(RoomDto roomDto) {
        updateRoomUseCase.updateRoom(this.roomMapper.mapToAggregate(roomDto));
        return ResponseEntity.status(HttpStatus.OK).body(roomDto);
    }

}
