package com.hotel.infrastructure.controller.mapper;

import java.util.UUID;

import com.hotel.domain.model.RoomId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    componentModel = "spring")
public interface RoomIdMapper {

  default UUID mapRoomIdValue(final RoomId id) {
    return null != id ? id.value() : null;
  }

}
