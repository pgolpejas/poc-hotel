package com.reservation.domain.usecase;

import com.reservation.application.controller.ReservationController;
import com.reservation.application.dto.ReservationDto;
import com.reservation.utils.BaseTestContainer;
import com.reservation.utils.RequestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.UUID;

class ReservationControllerIT extends BaseTestContainer {

    @Autowired
    protected TestRestTemplate restTemplate;

    @Test
    void when_reservation_exists_should_return_it() throws Exception {
        String reservationId = "d1a97f69-7fa0-4301-b498-128d78860828";

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
    void when_reservation_incorrect_should_not_create_it() throws Exception {
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
        Assertions.assertThat(answer.getProperties()).as("properties").isNull();
        Assertions.assertThat(answer.getDetail()).as("detail").isNotNull();
    }

}
