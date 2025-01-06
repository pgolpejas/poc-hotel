package com.hotel.infrastructure.controller.mapper;

import com.hotel.domain.model.Room;
import com.hotel.openapi.model.RoomDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(uses = {
    RoomIdMapper.class,
    HotelIdMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    componentModel = "spring")
public interface RoomMapper {

  RoomDto mapToDTO(Room aggregate);

  Room mapToAggregate(RoomDto entity);
}
