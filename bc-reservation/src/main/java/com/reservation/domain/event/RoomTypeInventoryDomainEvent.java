package com.reservation.domain.event;

import java.time.LocalDate;
import java.util.UUID;

import com.hotel.core.domain.ddd.DomainEvent;
import lombok.Builder;

public sealed interface RoomTypeInventoryDomainEvent extends DomainEvent {

  @Builder
  record RoomTypeInventoryCreatedEvent(UUID id, int version, UUID hotelId, Integer roomTypeId, LocalDate roomTypeInventoryDate,
                                       long totalInventory, long totalReserved) implements RoomTypeInventoryDomainEvent {

    @Override
    public String getAggregateId() {
      return this.id.toString();
    }

    @Override
    public String getActionType() {
      return "RoomTypeInventoryCreated";
    }
  }

  @Builder
  record RoomTypeInventoryUpdatedEvent(UUID id, int version, UUID hotelId, Integer roomTypeId, LocalDate roomTypeInventoryDate,
                                       long totalInventory, long totalReserved) implements RoomTypeInventoryDomainEvent {

    @Override
    public String getAggregateId() {
      return this.id.toString();
    }

    @Override
    public String getActionType() {
      return "RoomTypeInventoryUpdated";
    }
  }

  @Builder
  record RoomTypeInventoryDeletedEvent(UUID id) implements RoomTypeInventoryDomainEvent {

    @Override
    public String getAggregateId() {
      return this.id.toString();
    }

    @Override
    public String getActionType() {
      return "RoomTypeInventoryDeleted";
    }
  }
}
