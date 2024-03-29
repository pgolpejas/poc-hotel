package com.pocspringbootkafka.hotelavailability.ports;

import com.pocspringbootkafka.hotelavailability.domain.model.HotelAvailabilitySearch;
import com.pocspringbootkafka.shared.utils.SearchIdGenerator;
import com.pocspringbootkafka.utils.BaseTestContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

class HotelAvailabilityMessagingProducerServiceIT extends BaseTestContainer {

    @Autowired
    private HotelAvailabilityMessagingProducerService hotelAvailabilityMessagingProducerService;

    private static String searchId;
    private static final String hotelId = "1234Abc";
    private static HotelAvailabilitySearch searchDto;

    @BeforeAll
    static void init() {
        searchDto = new HotelAvailabilitySearch(hotelId, LocalDate.now(), LocalDate.now(), List.of(1, 2, 3));
        searchId = SearchIdGenerator.generateSearchId(searchDto);
    }

    @Test
    void sendMessage() {
        Assertions.assertDoesNotThrow(() -> hotelAvailabilityMessagingProducerService.sendMessage(searchDto));
    }
}