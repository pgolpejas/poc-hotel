package com.reservation.domain.event;

import java.io.Serial;
import java.time.LocalDate;
import java.util.UUID;

import com.hotel.core.domain.ddd.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RoomTypeInventoryUpdatedEvent implements DomainEvent {

  @Serial
  private static final long serialVersionUID = 1L;

  private UUID id;

  private int version;

  private UUID hotelId;

  Integer roomTypeId;
  
  private LocalDate roomTypeInventoryDate;

  private long totalInventory;

  private long totalReserved;

  public String getAggregateId() {
    return this.id.toString();
  }

  @Override
  public String getActionType() {
    return "RoomTypeInventoryUpdated";
  }
}
