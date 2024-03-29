package com.pocspringbootkafka.hotelavailability.ports;

import com.pocspringbootkafka.hotelavailability.domain.model.HotelAvailabilitySearch;

public interface HotelAvailabilityMessagingProducerService {

    void sendMessage(HotelAvailabilitySearch search);

}
