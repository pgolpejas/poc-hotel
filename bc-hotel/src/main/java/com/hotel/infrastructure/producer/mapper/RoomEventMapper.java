package com.hotel.infrastructure.producer.mapper;

import com.hotel.domain.event.RoomDomainEvent.RoomCreatedEvent;
import com.hotel.domain.event.RoomDomainEvent.RoomDeletedEvent;
import com.hotel.domain.event.RoomDomainEvent.RoomUpdatedEvent;
import com.hotel.domain.model.HotelId;
import com.hotel.domain.model.Room;
import com.hotel.domain.model.RoomId;
import com.room.domain.avro.v1.RoomCreated;
import com.room.domain.avro.v1.RoomDeleted;
import com.room.domain.avro.v1.RoomSnapshot;
import com.room.domain.avro.v1.RoomUpdated;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
@SuppressWarnings("java:S6204")
public interface RoomEventMapper {

    RoomSnapshot mapRoom(Room aggregate);

    RoomCreated mapRoomCreated(RoomCreatedEvent event);

    RoomDeleted mapRoomDeleted(RoomDeletedEvent event);

    RoomUpdated mapRoomUpdated(RoomUpdatedEvent event);

    default UUID mapId(final RoomId id) {
        return id.value();
    }

    default UUID mapHotelId(final HotelId hotelId) {
        return hotelId.value();
    }

}
