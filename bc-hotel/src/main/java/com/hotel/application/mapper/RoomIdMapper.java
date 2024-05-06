package com.hotel.application.mapper;

import com.hotel.domain.model.RoomId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring")
public interface RoomIdMapper {

  default UUID mapRoomIdValue(final RoomId id) {
    return null != id ? id.value() : null;
  }

}
