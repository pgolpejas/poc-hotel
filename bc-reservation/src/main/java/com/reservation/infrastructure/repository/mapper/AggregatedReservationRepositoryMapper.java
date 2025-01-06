package com.reservation.infrastructure.repository.mapper;

import com.reservation.domain.model.AggregatedReservation;
import com.reservation.infrastructure.controller.mapper.HotelIdMapper;
import com.reservation.infrastructure.repository.entity.AggregatedReservationEntity;
import com.reservation.infrastructure.repository.mongo.entity.AggregatedReservationDocument;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {
    HotelIdMapper.class,
}, injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    componentModel = "spring")
public interface AggregatedReservationRepositoryMapper {

  AggregatedReservationEntity mapToEntity(AggregatedReservation aggregate);

  AggregatedReservation mapToAggregate(AggregatedReservationEntity entity);

  @Mapping(target = "reservation", source = ".")
  @Mapping(target = "hotelId", source = "hotel.id")
  AggregatedReservation mapToAggregate(AggregatedReservationDocument document);

  @Mapping(target = ".", source = "reservation")
  AggregatedReservationDocument mapToDocument(AggregatedReservation aggregate);

}
