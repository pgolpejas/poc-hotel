package com.hotel.infrastructure.repository.mapper;

import com.hotel.domain.model.Hotel;
import com.hotel.infrastructure.entity.HotelEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(uses = {
        HotelIdRepositoryMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring")
public interface HotelRepositoryMapper {

    HotelEntity mapToEntity(Hotel aggregate);

    Hotel mapToAggregate(HotelEntity entity);

}
