package com.reservation.infrastructure.repository.mapper;

import com.reservation.domain.model.RoomTypeInventoryId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring")
public interface RoomTypeInventoryIdRepositoryMapper {

  default RoomTypeInventoryId mapRoomTypeInventoryId(final UUID id) {
    return null != id ? new RoomTypeInventoryId(id) : null;
  }

  default UUID mapRoomTypeInventoryIdValue(final RoomTypeInventoryId id) {
    return null != id ? id.value() : null;
  }

}
