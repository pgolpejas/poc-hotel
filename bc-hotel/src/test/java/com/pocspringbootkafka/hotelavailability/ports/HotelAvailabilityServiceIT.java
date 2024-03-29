package com.pocspringbootkafka.hotelavailability.ports;

import com.pocspringbootkafka.hotelavailability.domain.model.HotelAvailabilityDbSearch;
import com.pocspringbootkafka.hotelavailability.domain.model.HotelAvailabilitySearch;
import com.pocspringbootkafka.shared.utils.SearchIdGenerator;
import com.pocspringbootkafka.utils.BaseTestContainer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HotelAvailabilityServiceIT extends BaseTestContainer {

    @Autowired
    private HotelAvailabilityMessagingProducerService hotelAvailabilityMessagingProducerService;

    @Autowired
    private HotelAvailabilityRepository hotelAvailabilityRepository;

    @Autowired
    private HotelAvailabilityService hotelAvailabilityService;

    private static String searchId;
    private static final String hotelId = "1234AGT";
    private static HotelAvailabilitySearch searchDto;

    @BeforeAll
    static void init() {
        searchDto = new HotelAvailabilitySearch(hotelId, LocalDate.now(), LocalDate.now(), List.of(1, 2, 3));
        searchId = SearchIdGenerator.generateSearchId(searchDto);
    }

    @Test
    @Order(1)
    void search() {
        String searchIdReturn = hotelAvailabilityService.search(searchDto);
        Assertions.assertThat(searchIdReturn).as("searchId").isEqualTo(searchId);
    }

    private final CountDownLatch waiter = new CountDownLatch(1);

    @Test
    @Order(2)
    void findById() throws InterruptedException {
        waiter.await(1000 * 500000, TimeUnit.NANOSECONDS); // 500ms

        HotelAvailabilityDbSearch searchReturn = hotelAvailabilityService.findById(searchId);
        Assertions.assertThat(searchReturn.searchId()).as("searchId").isEqualTo(searchId);
        Assertions.assertThat(searchReturn.hotelId()).as("hotelId").isEqualTo(hotelId);
        Assertions.assertThat(searchReturn.count()).as("count").isEqualTo(1);

    }
}