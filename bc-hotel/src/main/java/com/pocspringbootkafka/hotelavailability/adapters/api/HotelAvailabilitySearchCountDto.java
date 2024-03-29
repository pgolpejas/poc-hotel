package com.pocspringbootkafka.hotelavailability.adapters.api;


public record HotelAvailabilitySearchCountDto(

    String searchId,

    HotelAvailabilitySearchDto search,

    Integer count) {

}

