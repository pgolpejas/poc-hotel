package com.pocspringbootkafka.hotelavailability.adapters.api;

import com.pocspringbootkafka.hotelavailability.domain.model.HotelAvailabilitySearch;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface HotelAvailabilityDtoMapper {

    HotelAvailabilitySearchDto toHotelAvailabilitySearchDto(HotelAvailabilitySearch search);

    HotelAvailabilitySearch toHotelAvailabilitySearch(HotelAvailabilitySearchDto searchDto);

}
