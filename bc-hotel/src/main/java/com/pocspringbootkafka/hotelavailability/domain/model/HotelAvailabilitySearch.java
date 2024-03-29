package com.pocspringbootkafka.hotelavailability.domain.model;

import java.time.LocalDate;
import java.util.List;

public record HotelAvailabilitySearch(
    String hotelId,

    LocalDate checkIn,

    LocalDate checkOut,

    List<Integer> ages) {


}

