package com.reservation.infrastructure.controller.mapper;

import java.util.UUID;

import com.reservation.domain.model.GuestId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    componentModel = "spring")
public interface GuestIdMapper {

  default UUID mapGuestIdValue(final GuestId id) {
    return null != id ? id.value() : null;
  }

}
