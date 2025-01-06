package com.reservation.infrastructure.repository.mapper;

import com.reservation.domain.model.RoomTypeInventory;
import com.reservation.infrastructure.repository.entity.RoomTypeInventoryEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(uses = {
    RoomTypeInventoryIdRepositoryMapper.class,
    GuestIdRepositoryMapper.class,
    HotelIdRepositoryMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    componentModel = "spring")
public interface RoomTypeInventoryRepositoryMapper {

  RoomTypeInventoryEntity mapToEntity(RoomTypeInventory aggregate);

  RoomTypeInventory mapToAggregate(RoomTypeInventoryEntity entity);

}
