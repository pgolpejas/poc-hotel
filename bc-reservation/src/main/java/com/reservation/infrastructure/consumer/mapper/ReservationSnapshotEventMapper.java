package com.reservation.infrastructure.consumer.mapper;

import com.reservation.domain.avro.v1.ReservationSnapshot;
import com.reservation.domain.model.AggregatedReservation.Reservation;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {CharSequenceMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    componentModel = "spring")
public interface ReservationSnapshotEventMapper {

  @Mapping(target = "hotel.id", source = "hotelId")
  Reservation mapToReservation(ReservationSnapshot msg);

}
