package com.pochotel.reservation.adapters.api;


public record HotelAvailabilitySearchCountDto(

    String searchId,

    HotelAvailabilitySearchDto search,

    Integer count) {

}

