package com.reservation.domain.repository;

import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.PaginationResponse;
import com.reservation.domain.model.Reservation;
import com.reservation.utils.BaseTestContainer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
                .limit(10)
                .sortBy("id")
                .sortDirection("ASC")
                .build();

        final PaginationResponse<Reservation> pageResponse = reservationRepository.searchBySelection(criteria);

        Assertions.assertThat(pageResponse).as("pageResponse").isNotNull();
        Assertions.assertThat(pageResponse.pagination()).as("pagination").isNotNull();
        Assertions.assertThat(pageResponse.data()).as("data").isNotEmpty();
        Assertions.assertThat(pageResponse.data().getFirst().getAggregateId()).as("aggregateId").hasToString(reservationId);
        Assertions.assertThat(pageResponse.data().getFirst().status()).as("status").isBlank();
    }

    @Test
    void when_reservation_filter_should_return_it() {
        final String reservationId = "d1a97f69-7fa0-4301-b498-128d78860828";
        final String filter = String.format("id:'%s'", reservationId);
        final Criteria criteria = Criteria.builder()
                .filters(filter)
                .page(0)
                .limit(10)
                .sortBy("id")
                .sortDirection("ASC")
                .build();

        final PaginationResponse<Reservation> pageResponse = reservationRepository.search(criteria);

        Assertions.assertThat(pageResponse).as("pageResponse").isNotNull();
        Assertions.assertThat(pageResponse.pagination()).as("pagination").isNotNull();
        Assertions.assertThat(pageResponse.data()).as("data").isNotEmpty();
        Assertions.assertThat(pageResponse.data().getFirst().getAggregateId()).as("aggregateId").hasToString(reservationId);
    }

    @Test
    @Transactional
    void when_reservation_created_javers_audited_it_and_return_shadows() {
        UUID id = UUID.randomUUID();
        UUID hotelId = UUID.randomUUID();
        Integer roomTypeId = 1;
        UUID guestId = UUID.randomUUID();
        final Reservation reservation = Reservation.builder()
                .id(id)
                .hotelId(hotelId)
                .roomTypeId(roomTypeId)
                .guestId(guestId)
                .start(LocalDate.now())
                .end(LocalDate.now())
                .status("ON")
                .build();
        reservationRepository.save(reservation);

        final AuditFilters filters = AuditFilters.builder()
                .id(id)
                .from(LocalDateTime.now().minusYears(1))
                .to(LocalDateTime.now())
                .build();
        final List<Reservation> plannedProductList = this.reservationRepository.findAuditByFilters(filters, 10);

        assertThat(plannedProductList).hasSize(1);

    }

}
