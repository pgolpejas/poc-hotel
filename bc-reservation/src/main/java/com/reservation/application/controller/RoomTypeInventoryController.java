package com.reservation.application.controller;

import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.reservation.openapi.model.RoomTypeInventoryDto;
import com.reservation.openapi.model.RoomTypeInventoryListResponse;
import com.reservation.application.mapper.CriteriaMapper;
import com.reservation.application.mapper.RoomTypeInventoryMapper;
import com.reservation.domain.model.RoomTypeInventory;
import com.reservation.domain.usecase.*;
import com.reservation.openapi.api.RoomTypeInventoryApi;
import com.reservation.openapi.model.*;
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
public class RoomTypeInventoryController implements RoomTypeInventoryApi {
    private final CreateRoomTypeInventoryUseCase createRoomTypeInventoryUseCase;
    private final UpdateRoomTypeInventoryUseCase updateRoomTypeInventoryUseCase;
    private final DeleteRoomTypeInventoryUseCase deleteRoomTypeInventoryUseCase;
    private final GetRoomTypeInventoryUseCase getRoomTypeInventoryUseCase;
    private final GetRoomTypeInventoriesUseCase getRoomTypeInventoriesUseCase;
    private final GetRoomTypeInventoriesAuditUseCase getRoomTypeInventoriesAuditUseCase;
    private final RoomTypeInventoryMapper roomTypeInventoryMapper;
    private final CriteriaMapper criteriaMapper;

    @Override
    public ResponseEntity<RoomTypeInventoryDto> createRoomTypeInventory(RoomTypeInventoryDto roomTypeInventoryDto) {
        createRoomTypeInventoryUseCase.createRoomTypeInventory(this.roomTypeInventoryMapper.mapToAggregate(roomTypeInventoryDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(roomTypeInventoryDto);
    }

    @Override
    public ResponseEntity<RoomTypeInventoryDto> deleteRoomTypeInventory(UUID id) {
        this.deleteRoomTypeInventoryUseCase.deleteRoomTypeInventory(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<RoomTypeInventoryDto> getRoomTypeInventory(UUID id) {
        return ResponseEntity.ok(this.roomTypeInventoryMapper.mapToDTO(this.getRoomTypeInventoryUseCase.getRoomTypeInventory(id)));
    }

    @Override
    public ResponseEntity<RoomTypeInventoryPaginationResponse> searchRoomTypeInventory(final CriteriaRequestDto criteriaRequestDto) {
        PaginationResponse<RoomTypeInventory> search = getRoomTypeInventoriesUseCase.getRoomTypeInventories(this.criteriaMapper.mapToAggregate(criteriaRequestDto));

        return ResponseEntity.ok(new RoomTypeInventoryPaginationResponse()
                .pagination(new Pagination()
                        .limit(search.pagination().limit())
                        .page(search.pagination().page())
                        .total(search.pagination().total())
                )
                .data(search.data().stream().map(this.roomTypeInventoryMapper::mapToDTO).toList())
        );
    }

    @Override
    public ResponseEntity<RoomTypeInventoryListResponse> searchAuditRoomTypeInventory(Integer limit, AuditFiltersRequestDto auditFilters) {
        List<RoomTypeInventoryDto> search = getRoomTypeInventoriesAuditUseCase.getRoomTypeInventoriesAudit(AuditFilters.builder()
                        .id(auditFilters.getId())
                        .from(auditFilters.getFrom())
                        .to(auditFilters.getTo())
                        .build(), limit).stream()
                .map(this.roomTypeInventoryMapper::mapToDTO).toList();
        return ResponseEntity.ok(new RoomTypeInventoryListResponse().data(search));
    }

    @Override
    public ResponseEntity<RoomTypeInventoryDto> updateRoomTypeInventory(RoomTypeInventoryDto roomTypeInventoryDto) {
        updateRoomTypeInventoryUseCase.updateRoomTypeInventory(this.roomTypeInventoryMapper.mapToAggregate(roomTypeInventoryDto));
        return ResponseEntity.status(HttpStatus.OK).body(roomTypeInventoryDto);
    }

}
