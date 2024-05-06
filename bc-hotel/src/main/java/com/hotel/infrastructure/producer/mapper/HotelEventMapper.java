package com.hotel.infrastructure.producer.mapper;

import com.hotel.domain.avro.v1.HotelCreated;
import com.hotel.domain.avro.v1.HotelDeleted;
import com.hotel.domain.avro.v1.HotelSnapshot;
import com.hotel.domain.avro.v1.HotelUpdated;
import com.hotel.domain.event.HotelDomainEvent.HotelCreatedEvent;
import com.hotel.domain.event.HotelDomainEvent.HotelDeletedEvent;
import com.hotel.domain.event.HotelDomainEvent.HotelUpdatedEvent;
import com.hotel.domain.model.Hotel;
import com.hotel.domain.model.HotelId;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
@SuppressWarnings("java:S6204")
public interface HotelEventMapper {

    HotelSnapshot mapHotel(Hotel aggregate);

    HotelCreated mapHotelCreated(HotelCreatedEvent event);

    HotelDeleted mapHotelDeleted(HotelDeletedEvent event);

    HotelUpdated mapHotelUpdated(HotelUpdatedEvent event);

    default UUID mapId(final HotelId id) {
        return id.value();
    }

}
