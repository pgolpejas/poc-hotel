package com.pocspringbootkafka.hotelavailability.ports;

import com.pocspringbootkafka.hotelavailability.domain.model.HotelAvailabilityDbSearch;
import com.pocspringbootkafka.hotelavailability.domain.model.HotelAvailabilitySearch;

public interface HotelAvailabilityService {

    String search(HotelAvailabilitySearch search);

    HotelAvailabilityDbSearch findById(String searchId);

}
