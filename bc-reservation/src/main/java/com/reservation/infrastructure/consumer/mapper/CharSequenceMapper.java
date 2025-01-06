package com.reservation.infrastructure.consumer.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    componentModel = "spring")
public interface CharSequenceMapper {

  default String mapCharSequenceToString(final CharSequence value) {
    return value != null ? value.toString() : null;
  }

}
