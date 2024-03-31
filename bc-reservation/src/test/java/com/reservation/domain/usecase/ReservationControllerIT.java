package com.reservation.domain.usecase;

import com.hotel.core.application.dto.CriteriaDto;
import com.hotel.core.domain.dto.ListResponse;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.reservation.application.controller.ReservationController;
import com.reservation.application.dto.ReservationDto;
import com.reservation.utils.BaseTestContainer;
import com.reservation.utils.RequestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

class ReservationControllerIT extends BaseTestContainer {

    @Autowired
    protected TestRestTemplate restTemplate;

    @Nested
    class FindById {

        @Test
        @Sql({"/sql/reservation/single.sql"})
        void when_reservation_exists_should_return_it() throws Exception {
            String reservationId = "d7a97f69-7fa0-4301-b498-128d78860828";

            final ResponseEntity<ReservationDto> response =
                    restTemplate.exchange(ReservationController.MAPPING + ReservationController.FIND_BY_ID_PATH,
                            HttpMethod.GET,
                            RequestUtils.buildRequest(null, null),
                            ReservationDto.class,
                            reservationId);

            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            ReservationDto answer = response.getBody();
            Assertions.assertThat(answer).as("reservation")
                    .isNotNull();
        }

        @Test
        void when_reservation_not_exists_should_return_not_found() throws Exception {
            String reservationId = UUID.randomUUID().toString();

            final ResponseEntity<ReservationDto> response =
                    restTemplate.exchange(ReservationController.MAPPING + ReservationController.FIND_BY_ID_PATH,
                            HttpMethod.GET,
                            RequestUtils.buildRequest(null, null),
                            ReservationDto.class,
                            reservationId);

            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    class Search {

        @Test
        @Sql({"/sql/reservation/single.sql"})
        void when_reservations_exists_filter_and_should_return_it() throws Exception {
            final String filter = "id:'d7a97f69-7fa0-4301-b498-128d78860828'";
            final CriteriaDto criteria = CriteriaDto.builder()
                    .filters(filter)
                    .page(0)
                    .limit(10)
                    .build();

            final ResponseEntity<PaginationResponse> response =
                    restTemplate.exchange(ReservationController.MAPPING + ReservationController.SEARCH_PATH,
                            HttpMethod.POST,
                            RequestUtils.buildRequest(null, criteria),
                            PaginationResponse.class,
                            criteria);

            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            PaginationResponse<?> answer = response.getBody();
            Assertions.assertThat(answer).as("reservations").isNotNull();
        }

        @Test
        @Sql({"/sql/reservation/single.sql"})
        void when_reservations_exists_filter_with_sort_and_should_return_it() throws Exception {
            final String filter = "id:'d7a97f69-7fa0-4301-b498-128d78860828'";
            final CriteriaDto criteria = CriteriaDto.builder()
                    .filters(filter)
                    .page(0)
                    .limit(10)
                    .sortBy("id")
                    .sortDirection("ASC")
                    .build();

            final ResponseEntity<PaginationResponse> response =
                    restTemplate.exchange(ReservationController.MAPPING + ReservationController.SEARCH_PATH,
                            HttpMethod.POST,
                            RequestUtils.buildRequest(null, criteria),
                            PaginationResponse.class,
                            criteria);

            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            var answer = response.getBody();
            Assertions.assertThat(answer).as("reservations").isNotNull();
            Assertions.assertThat(answer.data()).as("reservations").hasSize(1);

        }

        @Test
        void when_reservations_not_exists_filter_with_sort_and_should_return_empty() throws Exception {
            final String filter = "id:'11a97f69-7fa0-4301-b498-128d78860828'";
            final CriteriaDto criteria = CriteriaDto.builder()
                    .filters(filter)
                    .page(0)
                    .limit(10)
                    .sortBy("id")
                    .sortDirection("ASC")
                    .build();

            final ResponseEntity<PaginationResponse> response =
                    restTemplate.exchange(ReservationController.MAPPING + ReservationController.SEARCH_PATH,
                            HttpMethod.POST,
                            RequestUtils.buildRequest(null, criteria),
                            PaginationResponse.class,
                            criteria);

            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            var answer = response.getBody();
            Assertions.assertThat(answer).as("reservations").isNotNull();
            Assertions.assertThat(answer.data()).as("reservations").isEmpty();
        }
    }

    @Nested
    class SearchAudit {

        @Test
        void when_reservation_has_audit_return_it() throws Exception {
            UUID reservationId = UUID.randomUUID();
            final AuditFilters filters = AuditFilters.builder()
                    .id(reservationId)
                    .build();

            int limit = 10;
            final ResponseEntity<ListResponse> response =
                    restTemplate.exchange(ReservationController.MAPPING + ReservationController.SEARCH_AUDIT_PATH,
                            HttpMethod.POST,
                            RequestUtils.buildRequest(null, filters),
                            ListResponse.class, limit);

            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            ListResponse<?> answer = response.getBody();
            Assertions.assertThat(answer).as("reservations").isNotNull();
        }
    }

    @Nested
    class Create {

        @Test
        void when_reservation_should_create_it() throws Exception {
            UUID reservationId = UUID.randomUUID();
            final ReservationDto reservationDTO = ReservationDto.builder()
                    .id(reservationId)
                    .hotelId(UUID.randomUUID())
                    .roomTypeId(1)
                    .guestId(UUID.randomUUID())
                    .start(LocalDate.now())
                    .end(LocalDate.now())
                    .status("ON")
                    .build();

            final ResponseEntity<ReservationDto> response = restTemplate.exchange(ReservationController.MAPPING,
                    HttpMethod.POST,
                    RequestUtils.buildRequest(null, reservationDTO),
                    ReservationDto.class);

            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            ReservationDto answer = response.getBody();
            Assertions.assertThat(answer).as("reservation")
                    .isNotNull();
        }

        @Test
        void when_reservation_incorrect_data_should_not_create_it() throws Exception {
            UUID reservationId = UUID.randomUUID();
            final ReservationDto reservationDTO = ReservationDto.builder()
                    .id(reservationId)
                    .hotelId(UUID.randomUUID())
                    .roomTypeId(1)
                    .guestId(UUID.randomUUID())
                    .start(LocalDate.now())
                    .end(LocalDate.now().minusDays(2))
                    .status("ON_1234567890_1234567890")
                    .build();

            final ResponseEntity<ProblemDetail> response = restTemplate.exchange(ReservationController.MAPPING,
                    HttpMethod.POST,
                    RequestUtils.buildRequest(null, reservationDTO),
                    ProblemDetail.class);

            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

            ProblemDetail answer = response.getBody();
            Assertions.assertThat(answer).as("problemDetail").isNotNull();
            Assertions.assertThat(answer.getProperties()).as("properties").isNotNull();
            Assertions.assertThat(answer.getDetail()).as("detail").isNotNull();
        }

        @Test
        @Sql({"/sql/reservation/single.sql"})
        void when_reservation_already_exist_should_return_conflict() {
            UUID reservationId = UUID.fromString("d7a97f69-7fa0-4301-b498-128d78860828");
            final ReservationDto reservationDTO = ReservationDto.builder()
                    .id(reservationId)
                    .hotelId(UUID.randomUUID())
                    .roomTypeId(1)
                    .guestId(UUID.randomUUID())
                    .start(LocalDate.now())
                    .end(LocalDate.now())
                    .status("ON")
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PROBLEM_JSON));

            HttpEntity<?> request = new HttpEntity<>(reservationDTO, headers);

            final ResponseEntity<ProblemDetail> response = restTemplate.exchange(ReservationController.MAPPING,
                    HttpMethod.POST,
                    request,
                    ProblemDetail.class);

            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        }
    }

    @Nested
    class Update {

        @Test
        void when_reservation_not_exists_should_not_found_exception() throws Exception {
            UUID reservationId = UUID.randomUUID();
            final ReservationDto reservationDTO = ReservationDto.builder()
                    .id(reservationId)
                    .hotelId(UUID.randomUUID())
                    .roomTypeId(4)
                    .guestId(UUID.randomUUID())
                    .start(LocalDate.now())
                    .end(LocalDate.now())
                    .status("ON")
                    .build();

            final ResponseEntity<ReservationDto> response = restTemplate.exchange(ReservationController.MAPPING,
                    HttpMethod.PUT,
                    RequestUtils.buildRequest(null, reservationDTO),
                    ReservationDto.class);

            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @Sql({"/sql/reservation/single.sql"})
        void when_reservation_exists_should_update_it() throws Exception {
            UUID reservationId = UUID.fromString("d7a97f69-7fa0-4301-b498-128d78860828");
            final ReservationDto reservationDTO = ReservationDto.builder()
                    .id(reservationId)
                    .hotelId(UUID.randomUUID())
                    .roomTypeId(4)
                    .guestId(UUID.randomUUID())
                    .start(LocalDate.now())
                    .end(LocalDate.now())
                    .status("ON")
                    .build();

            final ResponseEntity<ReservationDto> response = restTemplate.exchange(ReservationController.MAPPING,
                    HttpMethod.PUT,
                    RequestUtils.buildRequest(null, reservationDTO),
                    ReservationDto.class);

            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            ReservationDto answer = response.getBody();
            Assertions.assertThat(answer).as("reservation").isNotNull();
            Assertions.assertThat(answer.roomTypeId()).as("roomTypeId").isEqualTo(4);
        }
    }

    @Nested
    class Delete {

        @Test
        @Sql({"/sql/reservation/single.sql"})
        void when_reservation_exists_should_delete_it() throws Exception {
            final String reservationId = "d7a97f69-7fa0-4301-b498-128d78860828";

            final ResponseEntity<Void> response =
                    restTemplate.exchange(ReservationController.MAPPING + ReservationController.DELETE_PATH,
                            HttpMethod.DELETE,
                            RequestUtils.buildRequest(null, null),
                            Void.class,
                            reservationId);

            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        void when_reservation_not_exists_and_delete_should_return_not_found() throws Exception {
            final String reservationId = UUID.randomUUID().toString();

            final ResponseEntity<Void> response =
                    restTemplate.exchange(ReservationController.MAPPING + ReservationController.DELETE_PATH,
                            HttpMethod.DELETE,
                            RequestUtils.buildRequest(null, null),
                            Void.class,
                            reservationId);

            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

}
