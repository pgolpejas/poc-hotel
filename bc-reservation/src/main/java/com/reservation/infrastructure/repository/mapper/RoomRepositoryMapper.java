package com.reservation.infrastructure.repository.mapper;

import com.reservation.domain.model.Room;
import com.reservation.infrastructure.repository.entity.HotelEntity.RoomEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    componentModel = "spring")
public interface RoomRepositoryMapper {

  Room mapToAggregate(RoomEntity entity);

  RoomEntity mapToEntity(Room aggregate);

}
