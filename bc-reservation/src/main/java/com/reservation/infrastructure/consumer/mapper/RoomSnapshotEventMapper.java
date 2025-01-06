package com.reservation.infrastructure.consumer.mapper;

import com.reservation.domain.model.Room;
import com.room.domain.avro.v1.RoomSnapshot;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(uses = {CharSequenceMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    componentModel = "spring")
public interface RoomSnapshotEventMapper {

  Room mapToRoom(RoomSnapshot msg);

}
