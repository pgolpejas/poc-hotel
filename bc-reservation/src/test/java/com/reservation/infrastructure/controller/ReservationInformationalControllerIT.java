package com.reservation.infrastructure.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.reservation.domain.model.Hotel;
import com.reservation.domain.model.Room;
import com.reservation.infrastructure.repository.mongo.entity.AggregatedReservationDocument;
import com.reservation.infrastructure.repository.mongo.repository.AggregatedReservationMongoRepository;
import com.reservation.openapi.model.AggregatedReservationCriteriaDto;
import com.reservation.openapi.model.AggregatedReservationPaginationResponse;
import com.reservation.utils.BaseITTest;
import com.reservation.utils.RequestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

class ReservationInformationalControllerIT extends BaseITTest {
  private static final String DELIMITER_PATH = "/";

  private static final String MAPPING = DELIMITER_PATH + "v1/informational/hotel-reservation";

  private static final String SEARCH_PATH = DELIMITER_PATH + "search";

  @Autowired
  protected TestRestTemplate restTemplate;

  @Autowired
  private AggregatedReservationMongoRepository aggregatedReservationMongoRepository;

  @AfterEach
  void tearDown() {
    this.aggregatedReservationMongoRepository.deleteAll();
  }

  @Nested
  class Search {

    @Test
    void when_reservations_filter_empty_should_return_exception() {
      final AggregatedReservationCriteriaDto criteria = new AggregatedReservationCriteriaDto()
          .sortBy("start_date")
          .sortDirection("ASC")
          .page(0)
          .limit(10);

      final ResponseEntity<AggregatedReservationPaginationResponse> response = ReservationInformationalControllerIT.this.restTemplate
          .exchange(MAPPING + SEARCH_PATH, HttpMethod.POST, RequestUtils.buildRequest(null, criteria),
              AggregatedReservationPaginationResponse.class, criteria);
      Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void when_reservations_filter_by_dates_out_of_range_should_return_exception() {
      final AggregatedReservationCriteriaDto criteria = new AggregatedReservationCriteriaDto()
          .from(LocalDate.parse("2025-01-01"))
          .to(LocalDate.parse("2025-07-02"))
          .sortBy("start_date")
          .sortDirection("ASC")
          .page(0)
          .limit(10);

      final ResponseEntity<AggregatedReservationPaginationResponse> response = ReservationInformationalControllerIT.this.restTemplate
          .exchange(MAPPING + SEARCH_PATH, HttpMethod.POST, RequestUtils.buildRequest(null, criteria),
              AggregatedReservationPaginationResponse.class, criteria);
      Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Sql({"/sql/aggregated-reservation/multiple_with_hotel_data.sql"})
    void when_reservations_filter_by_all_filters_should_return_it() {
      final AggregatedReservationCriteriaDto criteria = new AggregatedReservationCriteriaDto()
          .from(LocalDate.parse("2025-01-01"))
          .to(LocalDate.parse("2025-07-01"))
          .hotelIds(Set.of(UUID.fromString("d1a97f69-7fa0-4301-b498-128d78860828")))
          .guestIds(
              Set.of(UUID.fromString("f1a97f69-7fa0-4301-b498-128d78860828"), UUID.fromString("a1a97f69-7fa0-4301-b498-128d78860828")))
          .roomTypeIds(Set.of(1, 2))
          .hotelCountries(Set.of("USA"))
          .sortBy("start_date")
          .sortDirection("ASC")
          .page(0)
          .limit(10);

      final ResponseEntity<AggregatedReservationPaginationResponse> response = ReservationInformationalControllerIT.this.restTemplate
          .exchange(MAPPING + SEARCH_PATH, HttpMethod.POST, RequestUtils.buildRequest(null, criteria),
              AggregatedReservationPaginationResponse.class, criteria);
      Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

      final var answer = response.getBody();
      Assertions.assertThat(answer).as("reservations").isNotNull();
      Assertions.assertThat(answer.getData()).as("reservations").hasSize(2);

    }

    @Test
    @Sql({"/sql/aggregated-reservation/multiple_with_hotel_data.sql"})
    void when_reservations_filter_by_room_type_ids_should_return_it() {
      final AggregatedReservationCriteriaDto criteria = new AggregatedReservationCriteriaDto()
          .from(LocalDate.parse("2025-01-01"))
          .to(LocalDate.parse("2025-07-01"))
          .roomTypeIds(Set.of(2))
          .sortBy("start_date")
          .sortDirection("ASC")
          .page(0)
          .limit(10);

      final ResponseEntity<AggregatedReservationPaginationResponse> response = ReservationInformationalControllerIT.this.restTemplate
          .exchange(MAPPING + SEARCH_PATH, HttpMethod.POST, RequestUtils.buildRequest(null, criteria),
              AggregatedReservationPaginationResponse.class, criteria);
      Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

      final var answer = response.getBody();
      Assertions.assertThat(answer).as("reservations").isNotNull();
      Assertions.assertThat(answer.getData()).as("reservations").hasSize(1);
      Assertions.assertThat(answer.getData().getFirst().getRoomTypeId()).as("roomTypeId").isEqualTo(2);
    }

    @Test
    void when_reservations_filter_by_mongo_should_return_it() {
      final UUID guestId = UUID.randomUUID();
      final LocalDate startDate = LocalDate.parse("2025-01-01");
      final LocalDate endDate = LocalDate.parse("2025-07-01");
      final int roomTypeId = 1;
      final String country = "USA";
      final UUID hotelId = UUID.randomUUID();
      ReservationInformationalControllerIT.this.aggregatedReservationMongoRepository
          .save(buildAggregatedReservationDocument(startDate, endDate, guestId, roomTypeId, hotelId, country));

      ReservationInformationalControllerIT.this.aggregatedReservationMongoRepository
          .save(buildAggregatedReservationDocument(startDate.plusDays(1), endDate.plusDays(1), guestId, 2, hotelId, "SPAIN"));

      final AggregatedReservationCriteriaDto criteria = new AggregatedReservationCriteriaDto()
          .from(startDate)
          .to(endDate)
          .hotelIds(Set.of(hotelId))
          .roomTypeIds(Set.of(roomTypeId, 2))
          .guestIds(Set.of(guestId))
          .hotelCountries(Set.of(country, "SPAIN"))
          .sortBy("start_date")
          .sortDirection("ASC")
          .page(0)
          .limit(10)
          .database(AggregatedReservationCriteriaDto.DatabaseEnum.MONGO);

      final ResponseEntity<AggregatedReservationPaginationResponse> response = ReservationInformationalControllerIT.this.restTemplate
          .exchange(MAPPING + SEARCH_PATH, HttpMethod.POST, RequestUtils.buildRequest(null, criteria),
              AggregatedReservationPaginationResponse.class, criteria);
      Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

      final var answer = response.getBody();
      Assertions.assertThat(answer).as("reservations").isNotNull();
      Assertions.assertThat(answer.getData()).as("reservations").hasSize(2);
      Assertions.assertThat(answer.getData().stream().anyMatch(reservation -> reservation.getRoomTypeId() == roomTypeId))
          .as("roomTypeId").isTrue();
      Assertions.assertThat(answer.getData().stream().anyMatch(reservation -> reservation.getRoomTypeId() == 2))
          .as("roomTypeId").isTrue();
    }

    private static AggregatedReservationDocument buildAggregatedReservationDocument(final LocalDate startDate, final LocalDate endDate,
        final UUID guestId,
        final int roomTypeId, final UUID hotelId, final String country) {
      return AggregatedReservationDocument.builder()
          .id(UUID.randomUUID())
          .start(startDate)
          .end(endDate)
          .guestId(guestId)
          .roomTypeId(roomTypeId)
          .status("ON")
          .hotel(Hotel.builder()
              .id(hotelId)
              .name("Hotel")
              .country(country)
              .city("City")
              .address("Address")
              .postalCode("ZipCode")
              .rooms(List.of(Room.builder()
                  .floor(1)
                  .name("Room1")
                  .available(true)
                  .roomNumber("101")
                  .roomTypeId(roomTypeId)
                  .build()))
              .build())
          .build();
    }
  }

}
