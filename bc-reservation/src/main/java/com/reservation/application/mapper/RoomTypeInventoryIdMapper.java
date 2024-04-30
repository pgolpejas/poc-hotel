package com.reservation.application.mapper;

import com.reservation.domain.model.RoomTypeInventoryId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring")
public interface RoomTypeInventoryIdMapper {

  default UUID mapRoomTypeInventoryIdValue(final RoomTypeInventoryId id) {
    return null != id ? id.value() : null;
  }

}
