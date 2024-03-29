package com.pocspringbootkafka.hotelavailability.adapters.db;

import com.pocspringbootkafka.hotelavailability.domain.model.HotelAvailabilityDbSearch;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HotelAvailabilitySearchEntityMapper {

    HotelAvailabilitySearchEntity toHotelAvailabilitySearchEntity(HotelAvailabilityDbSearch search);

    HotelAvailabilityDbSearch toHotelAvailabilitySearch(HotelAvailabilitySearchEntity searchEntity);
}
