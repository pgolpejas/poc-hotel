package com.reservation.infrastructure.producer.mapper;

import java.util.UUID;

import com.reservation.domain.event.RoomTypeInventoryDomainEvent.RoomTypeInventoryCreatedEvent;
import com.reservation.domain.event.RoomTypeInventoryDomainEvent.RoomTypeInventoryDeletedEvent;
import com.reservation.domain.event.RoomTypeInventoryDomainEvent.RoomTypeInventoryUpdatedEvent;
import com.reservation.domain.model.GuestId;
import com.reservation.domain.model.HotelId;
import com.reservation.domain.model.RoomTypeInventory;
import com.reservation.domain.model.RoomTypeInventoryId;
import com.roomTypeInventory.domain.avro.v1.RoomTypeInventoryCreated;
import com.roomTypeInventory.domain.avro.v1.RoomTypeInventoryDeleted;
import com.roomTypeInventory.domain.avro.v1.RoomTypeInventorySnapshot;
import com.roomTypeInventory.domain.avro.v1.RoomTypeInventoryUpdated;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
@SuppressWarnings("java:S6204")
public interface RoomTypeInventoryEventMapper {

  RoomTypeInventorySnapshot mapRoomTypeInventory(RoomTypeInventory aggregate);

  RoomTypeInventoryCreated mapRoomTypeInventoryCreated(RoomTypeInventoryCreatedEvent event);

  RoomTypeInventoryDeleted mapRoomTypeInventoryDeleted(RoomTypeInventoryDeletedEvent event);

  RoomTypeInventoryUpdated mapRoomTypeInventoryUpdated(RoomTypeInventoryUpdatedEvent event);

  default UUID mapId(final RoomTypeInventoryId id) {
    return id.value();
  }

  default UUID mapHotelId(final HotelId hotelId) {
    return hotelId.value();
  }

  default UUID mapGuestId(final GuestId guestId) {
    return guestId.value();
  }
}
