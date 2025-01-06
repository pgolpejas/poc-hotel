package com.reservation.infrastructure.controller.mapper;

import java.util.UUID;

import com.reservation.domain.model.ReservationId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    componentModel = "spring")
public interface ReservationIdMapper {

  default UUID mapReservationIdValue(final ReservationId id) {
    return null != id ? id.value() : null;
  }

}
