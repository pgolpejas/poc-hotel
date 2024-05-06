package com.hotel.domain.event;

import com.hotel.core.domain.ddd.DomainEvent;
import lombok.Builder;

import java.util.UUID;

public sealed interface RoomDomainEvent extends DomainEvent {

    @Builder
    record RoomCreatedEvent(UUID id, int version, UUID hotelId, Integer roomTypeId, String name, int floor,
                            String roomNumber, boolean available) implements RoomDomainEvent {

        @Override
        public String getAggregateId() {
            return this.id.toString();
        }

        @Override
        public String getActionType() {
            return "RoomCreated";
        }
    }

    @Builder
    record RoomUpdatedEvent(UUID id, int version, UUID hotelId, Integer roomTypeId, String name, int floor,
                            String roomNumber, boolean available) implements RoomDomainEvent {

        @Override
        public String getAggregateId() {
            return this.id.toString();
        }

        @Override
        public String getActionType() {
            return "RoomUpdated";
        }
    }

    @Builder
    record RoomDeletedEvent(UUID id) implements RoomDomainEvent {

        @Override
        public String getAggregateId() {
            return this.id.toString();
        }

        @Override
        public String getActionType() {
            return "RoomDeleted";
        }
    }
}
