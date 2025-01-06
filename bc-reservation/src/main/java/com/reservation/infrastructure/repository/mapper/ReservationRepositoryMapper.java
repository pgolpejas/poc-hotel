package com.reservation.infrastructure.repository.mapper;

import com.reservation.domain.model.Reservation;
import com.reservation.infrastructure.repository.entity.ReservationEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(uses = {
    ReservationIdRepositoryMapper.class,
    GuestIdRepositoryMapper.class,
    HotelIdRepositoryMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    componentModel = "spring")
public interface ReservationRepositoryMapper {

  ReservationEntity mapToEntity(Reservation aggregate);

  Reservation mapToAggregate(ReservationEntity entity);

}
