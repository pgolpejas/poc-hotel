package com.hotel.infrastructure.controller.mapper;

import com.hotel.domain.model.Hotel;
import com.hotel.openapi.model.HotelDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(uses = {
    HotelIdMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    componentModel = "spring")
public interface HotelMapper {

  HotelDto mapToDTO(Hotel aggregate);

  Hotel mapToAggregate(HotelDto entity);

}
