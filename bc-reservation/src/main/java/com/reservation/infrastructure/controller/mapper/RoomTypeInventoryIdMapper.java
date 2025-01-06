package com.reservation.infrastructure.controller.mapper;

import java.util.UUID;

import com.reservation.domain.model.RoomTypeInventoryId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    componentModel = "spring")
public interface RoomTypeInventoryIdMapper {

  default UUID mapRoomTypeInventoryIdValue(final RoomTypeInventoryId id) {
    return null != id ? id.value() : null;
  }

}
