package com.pochotel.reservation.adapters.db;

import com.pochotel.reservation.domain.model.HotelAvailabilityDbSearch;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HotelAvailabilitySearchEntityMapper {

    HotelAvailabilitySearchEntity toHotelAvailabilitySearchEntity(HotelAvailabilityDbSearch search);

    HotelAvailabilityDbSearch toHotelAvailabilitySearch(HotelAvailabilitySearchEntity searchEntity);
}
