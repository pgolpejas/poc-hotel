package com.hotel.domain.event;

import com.hotel.core.domain.ddd.DomainEvent;
import lombok.Builder;

import java.util.UUID;

public sealed interface HotelDomainEvent extends DomainEvent {

    @Builder
    record HotelCreatedEvent(UUID id, int version, String name, String address, String city, String state,
                             String country, String postalCode) implements HotelDomainEvent {

        @Override
        public String getAggregateId() {
            return this.id.toString();
        }

        @Override
        public String getActionType() {
            return "HotelCreated";
        }
    }

    @Builder
    record HotelUpdatedEvent(UUID id, int version, String name, String address, String city, String state,
                             String country, String postalCode) implements HotelDomainEvent {

        @Override
        public String getAggregateId() {
            return this.id.toString();
        }

        @Override
        public String getActionType() {
            return "HotelUpdated";
        }
    }

    @Builder
    record HotelDeletedEvent(UUID id) implements HotelDomainEvent {

        @Override
        public String getAggregateId() {
            return this.id.toString();
        }

        @Override
        public String getActionType() {
            return "HotelDeleted";
        }
    }
}
