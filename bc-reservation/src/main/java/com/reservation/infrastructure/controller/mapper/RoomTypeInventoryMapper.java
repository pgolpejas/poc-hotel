package com.reservation.infrastructure.controller.mapper;

import com.reservation.domain.model.RoomTypeInventory;
import com.reservation.openapi.model.RoomTypeInventoryDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(uses = {
    RoomTypeInventoryIdMapper.class,
    HotelIdMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    componentModel = "spring")
public interface RoomTypeInventoryMapper {

  RoomTypeInventoryDto mapToDTO(RoomTypeInventory aggregate);

  RoomTypeInventory mapToAggregate(RoomTypeInventoryDto entity);
}
