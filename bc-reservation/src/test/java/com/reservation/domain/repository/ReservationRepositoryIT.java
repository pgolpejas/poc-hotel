package com.reservation.domain.repository;

import com.reservation.domain.model.Reservation;
import com.reservation.domain.utils.Criteria;
import com.reservation.domain.utils.PageResponse;
import com.reservation.utils.BaseTestContainer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservationRepositoryIT extends BaseTestContainer {

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    void when_reservation_is_ok_should_create_it() {
        UUID hotelId = UUID.randomUUID();
        Integer roomTypeId = 1;
        UUID guestId = UUID.randomUUID();
        final Reservation reservation = Reservation.builder()
                .id(UUID.randomUUID())
                .hotelId(hotelId)
                .roomTypeId(roomTypeId)
                .guestId(guestId)
                .start(LocalDate.now())
                .end(LocalDate.now())
                .status("ON")
                .build();
        reservationRepository.save(reservation);

        Optional<Reservation> optSearch = reservationRepository.findById(reservation.getId().value());

        assertTrue(optSearch.isPresent());
        Assertions.assertThat(optSearch.get().toString()).as("toString").isNotBlank();
        Assertions.assertThat(optSearch.get().id()).as("id").isEqualTo(reservation.id());
        Assertions.assertThat(optSearch.get().id().toString()).as("aggregateId").hasToString(reservation.getAggregateId());
        Assertions.assertThat(optSearch.get().hotelId()).as("hotelId").isEqualTo(hotelId);
        Assertions.assertThat(optSearch.get().roomTypeId()).as("roomTypeId").isEqualTo(roomTypeId);
        Assertions.assertThat(optSearch.get().guestId()).as("guestId").isEqualTo(guestId);
    }

    @Test
    void when_reservation_filter_with_selection_should_return_it() {
        final String reservationId = "d1a97f69-7fa0-4301-b498-128d78860828";
        final String filter = String.format("id:'%s'", reservationId);
        final Criteria criteria = Criteria.builder()
                .filters(filter)
                .page(0)
                .size(10)
                .sortBy("id")
                .sortDirection("ASC")
                .build();

        final PageResponse<Reservation> pageResponse = reservationRepository.searchBySelection(criteria);

        Assertions.assertThat(pageResponse).as("pageResponse").isNotNull();
        Assertions.assertThat(pageResponse.items()).as("items").isNotEmpty();
        Assertions.assertThat(pageResponse.items().getFirst().getAggregateId()).as("aggregateId").hasToString(reservationId);
        Assertions.assertThat(pageResponse.items().getFirst().status()).as("status").isBlank();
    }

    @Test
    void when_reservation_filter_should_return_it() {
        final String reservationId = "d1a97f69-7fa0-4301-b498-128d78860828";
        final String filter = String.format("id:'%s'", reservationId);
        final Criteria criteria = Criteria.builder()
                .filters(filter)
                .page(0)
                .size(10)
                .sortBy("id")
                .sortDirection("ASC")
                .build();

        final PageResponse<Reservation> pageResponse = reservationRepository.search(criteria);

        Assertions.assertThat(pageResponse).as("pageResponse").isNotNull();
        Assertions.assertThat(pageResponse.items()).as("items").isNotEmpty();
        Assertions.assertThat(pageResponse.items().getFirst().getAggregateId()).as("aggregateId").hasToString(reservationId);
    }

}
