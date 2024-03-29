package com.pochotel.reservation.ports;

import com.pochotel.reservation.domain.model.HotelAvailabilityDbSearch;
import com.pochotel.reservation.domain.model.HotelAvailabilitySearch;

public interface HotelAvailabilityService {

    String search(HotelAvailabilitySearch search);

    HotelAvailabilityDbSearch findById(String searchId);

}
