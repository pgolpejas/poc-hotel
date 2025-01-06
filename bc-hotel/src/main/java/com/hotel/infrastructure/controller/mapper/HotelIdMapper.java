package com.hotel.infrastructure.controller.mapper;

import java.util.UUID;

import com.hotel.domain.model.HotelId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    componentModel = "spring")
public interface HotelIdMapper {

  default UUID mapHotelIdValue(final HotelId id) {
    return null != id ? id.value() : null;
  }

}
