package com.hotel.infrastructure.repository.mapper;

import com.hotel.domain.model.RoomId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring")
public interface RoomIdRepositoryMapper {

    default RoomId mapRoomId(final UUID id) {
        return null != id ? new RoomId(id) : null;
    }

    default UUID mapRoomIdValue(final RoomId id) {
        return null != id ? id.value() : null;
    }

}
