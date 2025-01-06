package com.reservation.infrastructure.consumer.mapper;

import com.hotel.domain.avro.v1.HotelSnapshot;
import com.reservation.domain.model.Hotel;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(uses = {CharSequenceMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    componentModel = "spring")
public interface HotelSnapshotEventMapper {

  Hotel mapToHotel(HotelSnapshot msg);

}
