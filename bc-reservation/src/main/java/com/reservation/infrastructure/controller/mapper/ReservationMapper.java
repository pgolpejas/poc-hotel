package com.reservation.infrastructure.controller.mapper;

import com.reservation.domain.model.AggregatedReservation;
import com.reservation.domain.model.Reservation;
import com.reservation.openapi.model.AggregatedReservationDto;
import com.reservation.openapi.model.ReservationDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(uses = {
    ReservationIdMapper.class,
    GuestIdMapper.class,
    HotelIdMapper.class
},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    componentModel = "spring")
public interface ReservationMapper {

  ReservationDto mapToDTO(Reservation aggregate);

  AggregatedReservationDto mapToAggregateResponseDTO(AggregatedReservation.Reservation aggregate);

  Reservation mapToAggregate(ReservationDto entity);
}
