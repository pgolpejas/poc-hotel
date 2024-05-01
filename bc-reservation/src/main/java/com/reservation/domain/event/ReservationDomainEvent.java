package com.reservation.domain.event;

import java.time.LocalDate;
import java.util.UUID;

import com.hotel.core.domain.ddd.DomainEvent;
import lombok.Builder;

public sealed interface ReservationDomainEvent extends DomainEvent {

  @Builder
  record ReservationCreatedEvent(UUID id, int version, UUID hotelId, Integer roomTypeId, UUID guestId, LocalDate start, LocalDate end,
                                 String status) implements ReservationDomainEvent {

    @Override
    public String getAggregateId() {
      return this.id.toString();
    }

    @Override
    public String getActionType() {
      return "ReservationCreated";
    }
  }

  @Builder
  record ReservationUpdatedEvent(UUID id, int version, UUID hotelId, Integer roomTypeId, UUID guestId, LocalDate start, LocalDate end,
                                 String status) implements ReservationDomainEvent {

    @Override
    public String getAggregateId() {
      return this.id.toString();
    }

    @Override
    public String getActionType() {
      return "ReservationUpdated";
    }
  }

  @Builder
  record ReservationDeletedEvent(UUID id) implements ReservationDomainEvent {

    @Override
    public String getAggregateId() {
      return this.id.toString();
    }

    @Override
    public String getActionType() {
      return "ReservationDeleted";
    }
  }
}
