package com.pocspringbootkafka.hotelavailability.adapters.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pocspringbootkafka.hotelavailability.domain.model.HotelAvailabilitySearch;
import com.pocspringbootkafka.hotelavailability.ports.HotelAvailabilityMessagingProducerService;
import com.pocspringbootkafka.hotelavailability.ports.HotelAvailabilityRepository;
import com.pocspringbootkafka.hotelavailability.ports.HotelAvailabilityService;
import com.pocspringbootkafka.shared.utils.SearchIdGenerator;
import com.pocspringbootkafka.utils.BaseTestContainer;
import com.pocspringbootkafka.utils.RequestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SuppressWarnings("squid:S5976")
class HotelAvailabilityControllerIT extends BaseTestContainer {

    @Autowired
    private KafkaTemplate<String, HotelAvailabilitySearch> kafkaTemplate;

    @Autowired
    private HotelAvailabilityDtoMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HotelAvailabilityMessagingProducerService hotelAvailabilityMessagingProducerService;

    @Autowired
    private HotelAvailabilityRepository hotelAvailabilityRepository;

    @Autowired
    private HotelAvailabilityService hotelAvailabilityService;

    @Autowired
    private HotelAvailabilityController controller;

    @Autowired
    protected TestRestTemplate restTemplate;

    private final CountDownLatch waiter = new CountDownLatch(1);

    @Test
    @Order(10)
    void search() throws Exception {
        String hotelId = "4321Abc";
        HotelAvailabilitySearchDto searchDto = new HotelAvailabilitySearchDto(hotelId, LocalDate.now(), LocalDate.now(), List.of(1, 2, 3));
        String searchId = SearchIdGenerator.generateSearchId(mapper.toHotelAvailabilitySearch(searchDto));

        final ResponseEntity<String> response =
            restTemplate.exchange(HotelAvailabilityController.MAPPING + HotelAvailabilityController.SEARCH_PATH, HttpMethod.POST,
                RequestUtils.buildRequest(null, searchDto), String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        String answer = response.getBody();
        Assertions.assertThat(answer).as("searchId").isNotNull().isEqualTo(searchId);
    }

    @Test
    @Order(20)
    void searchWithNewHotelId() throws Exception {
        String hotelId = "1234CBA";
        HotelAvailabilitySearchDto searchDto = new HotelAvailabilitySearchDto(hotelId, LocalDate.now(), LocalDate.now(), List.of(3, 2, 1));
        String searchId = SearchIdGenerator.generateSearchId(mapper.toHotelAvailabilitySearch(searchDto));

        final ResponseEntity<String> response =
            restTemplate.exchange(HotelAvailabilityController.MAPPING + HotelAvailabilityController.SEARCH_PATH, HttpMethod.POST,
                RequestUtils.buildRequest(null, searchDto), String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        String answer = response.getBody();
        Assertions.assertThat(answer).as("searchId").isNotNull().isEqualTo(searchId);
    }

    @Test
    @Order(30)
    void searchSimilar() throws Exception {
        String hotelId = "4321Abc";
        HotelAvailabilitySearchDto searchDto = new HotelAvailabilitySearchDto(hotelId, LocalDate.now(), LocalDate.now(), List.of(3, 2, 1));
        String searchId = SearchIdGenerator.generateSearchId(mapper.toHotelAvailabilitySearch(searchDto));

        final ResponseEntity<String> response =
            restTemplate.exchange(HotelAvailabilityController.MAPPING + HotelAvailabilityController.SEARCH_PATH, HttpMethod.POST,
                RequestUtils.buildRequest(null, searchDto), String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        String answer = response.getBody();
        Assertions.assertThat(answer).as("searchId").isNotNull().isEqualTo(searchId);
    }



    @Test
    @Order(40)
    void count() throws Exception {
        // Waiting for the message to be consumed
        waiter.await(1000 * 500000, TimeUnit.NANOSECONDS); // 500ms

        String hotelId = "4321Abc";
        HotelAvailabilitySearchDto searchDto = new HotelAvailabilitySearchDto(hotelId, LocalDate.now(), LocalDate.now(), List.of(1, 2, 3));
        String searchId = SearchIdGenerator.generateSearchId(mapper.toHotelAvailabilitySearch(searchDto));

        final ResponseEntity<HotelAvailabilitySearchCountDto> response =
            restTemplate.exchange(HotelAvailabilityController.MAPPING + HotelAvailabilityController.COUNT_PATH, HttpMethod.GET,
                RequestUtils.buildRequest(null, null), HotelAvailabilitySearchCountDto.class, searchId);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        HotelAvailabilitySearchCountDto answer = response.getBody();
        Assertions.assertThat(answer).as("searchId").isNotNull();
        Assertions.assertThat(answer.count()).as("count").isEqualTo(2);
        Assertions.assertThat(answer.searchId()).as("searchId").isEqualTo(searchId);
        Assertions.assertThat(answer.search().hotelId()).as("hotelId").isEqualTo(hotelId);
    }



    @Test
    @Order(50)
    void countSearchWithNewHotelId() throws Exception {
        // Waiting for the message to be consumed
        waiter.await(1000 * 500000, TimeUnit.NANOSECONDS); // 500ms

        String hotelId = "1234CBA";
        HotelAvailabilitySearchDto searchDto = new HotelAvailabilitySearchDto(hotelId, LocalDate.now(), LocalDate.now(), List.of(1, 2, 3));
        String searchId = SearchIdGenerator.generateSearchId(mapper.toHotelAvailabilitySearch(searchDto));

        final ResponseEntity<HotelAvailabilitySearchCountDto> response =
            restTemplate.exchange(HotelAvailabilityController.MAPPING + HotelAvailabilityController.COUNT_PATH, HttpMethod.GET,
                RequestUtils.buildRequest(null, null), HotelAvailabilitySearchCountDto.class, searchId);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        HotelAvailabilitySearchCountDto answer = response.getBody();
        Assertions.assertThat(answer).as("searchId").isNotNull();
        Assertions.assertThat(answer.count()).as("count").isEqualTo(1);
        Assertions.assertThat(answer.searchId()).as("searchId").isEqualTo(searchId);
        Assertions.assertThat(answer.search().hotelId()).as("hotelId").isEqualTo(hotelId);
    }


}
