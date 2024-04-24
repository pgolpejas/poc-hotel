package com.reservation.application.mapper;

import com.reservation.domain.model.HotelId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring")
public interface HotelIdMapper {

    default UUID mapHotelIdValue(final HotelId id) {
        return null != id ? id.value() : null;
    }

}
