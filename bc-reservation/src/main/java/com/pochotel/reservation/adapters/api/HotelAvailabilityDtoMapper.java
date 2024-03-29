package com.pochotel.reservation.adapters.api;

import com.pochotel.reservation.domain.model.HotelAvailabilitySearch;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface HotelAvailabilityDtoMapper {

    HotelAvailabilitySearchDto toHotelAvailabilitySearchDto(HotelAvailabilitySearch search);

    HotelAvailabilitySearch toHotelAvailabilitySearch(HotelAvailabilitySearchDto searchDto);

}
