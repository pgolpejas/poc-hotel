package com.hotel.infrastructure.repository.mapper;

import com.hotel.domain.model.HotelId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring")
public interface HotelIdRepositoryMapper {

    default HotelId mapHotelId(final UUID id) {
        return null != id ? new HotelId(id) : null;
    }

    default UUID mapHotelIdValue(final HotelId id) {
        return null != id ? id.value() : null;
    }

}
