package com.reservation.application.mapper;

import com.reservation.domain.model.ReservationId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring")
public interface ReservationIdMapper {

  default UUID mapReservationIdValue(final ReservationId id) {
    return null != id ? id.value() : null;
  }

}
