package com.hotel.infrastructure.repository.mapper;

import com.hotel.domain.model.Room;
import com.hotel.infrastructure.entity.RoomEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(uses = {
        RoomIdRepositoryMapper.class,
        HotelIdRepositoryMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring")
public interface RoomRepositoryMapper {

    RoomEntity mapToEntity(Room aggregate);

    Room mapToAggregate(RoomEntity entity);

}
