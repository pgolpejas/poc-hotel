package com.reservation.infrastructure.repository.mapper;

import com.reservation.domain.model.ReservationId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring")
public interface ReservationIdRepositoryMapper {

  default ReservationId mapReservationId(final UUID id) {
    return null != id ? new ReservationId(id) : null;
  }

  default UUID mapReservationIdValue(final ReservationId id) {
    return null != id ? id.value() : null;
  }

}
