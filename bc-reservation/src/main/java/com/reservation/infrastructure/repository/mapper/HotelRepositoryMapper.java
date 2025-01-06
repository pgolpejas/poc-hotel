package com.reservation.infrastructure.repository.mapper;

import com.reservation.domain.model.Hotel;
import com.reservation.infrastructure.repository.entity.HotelEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(uses = {HotelIdRepositoryMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    componentModel = "spring")
public interface HotelRepositoryMapper {

  Hotel mapToAggregate(HotelEntity entity);

  HotelEntity mapToEntity(Hotel aggregate);

}
