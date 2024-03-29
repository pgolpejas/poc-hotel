package com.reservation.infrastructure.repository.mapper;

import com.reservation.domain.model.GuestId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring")
public interface GuestIdRepositoryMapper {

  default GuestId mapGuestId(final UUID id) {
    return null != id ? new GuestId(id) : null;
  }

  default UUID mapGuestIdValue(final GuestId id) {
    return null != id ? id.value() : null;
  }

}
