package com.pocspringbootkafka.hotelavailability.ports;

import com.pocspringbootkafka.hotelavailability.domain.model.HotelAvailabilityDbSearch;
import com.pocspringbootkafka.utils.BaseTestContainer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class HotelAvailabilityRepositoryIT extends BaseTestContainer {

    @Autowired
    private HotelAvailabilityMessagingProducerService hotelAvailabilityMessagingProducerService;

    @Autowired
    private HotelAvailabilityRepository hotelAvailabilityRepository;

    @Test
    void save() {
        String searchId= "3333";
        String hotelId = "2345";
        HotelAvailabilityDbSearch search=  new HotelAvailabilityDbSearch(searchId, hotelId, LocalDate.now(), LocalDate.now(),List.of(1,2,3),1);
        search=  hotelAvailabilityRepository.save(search);
        Optional<HotelAvailabilityDbSearch> optSearch = hotelAvailabilityRepository.findById(searchId);

        assertTrue(optSearch.isPresent());
        Assertions.assertThat(optSearch.get().searchId()).as("searchId").isEqualTo(searchId);
        Assertions.assertThat(optSearch.get().hotelId()).as("hotelId").isEqualTo(hotelId);
        Assertions.assertThat(optSearch.get().count()).as("count").isEqualTo(1);

    }
}