package com.reservation.application.mapper;

import com.reservation.application.dto.ReservationDto;
import com.reservation.domain.model.Reservation;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(uses = {
        ReservationIdMapper.class,
        GuestIdMapper.class,
        HotelIdMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring")
public interface ReservationMapper {

    ReservationDto mapToDTO(Reservation aggregate);

    Reservation mapToAggregate(ReservationDto entity);

}
