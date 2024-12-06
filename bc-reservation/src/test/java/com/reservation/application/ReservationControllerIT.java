package com.reservation.application;

import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.outbox.data.OutboxEntity;
import com.outbox.data.OutboxRepository;
import com.outbox.data.SnapshotEntity;
import com.reservation.openapi.model.CriteriaRequestDto;
import com.reservation.openapi.model.ReservationDto;
import com.reservation.openapi.model.ReservationListResponse;
import com.reservation.openapi.model.ReservationPaginationResponse;
import com.reservation.utils.BaseITTest;
import com.reservation.utils.RequestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

class ReservationControllerIT extends BaseITTest {
	private static final String DELIMITER_PATH = "/";
	private static final String MAPPING = DELIMITER_PATH + "v1/hotel-reservation";
	private static final String FIND_BY_ID_PATH = DELIMITER_PATH + "{id}";
	private static final String DELETE_PATH = DELIMITER_PATH + "{id}";
	private static final String SEARCH_PATH = DELIMITER_PATH + "search";
	private static final String SEARCH_AUDIT_PATH = DELIMITER_PATH + "search-audit/{limit}";

	@Autowired
	protected TestRestTemplate restTemplate;

	@Autowired
	@Qualifier("outboxSnapshotRepository")
	private OutboxRepository<SnapshotEntity> outboxSnapshotRepository;

	@Autowired
	@Qualifier("outboxDomainRepository")
	private OutboxRepository<OutboxEntity> outboxDomainRepository;

	@Nested
	class FindById {

		@Test
		@Sql({"/sql/reservation/single.sql"})
		void when_reservation_exists_should_return_it() throws Exception {
			final String reservationId = "d7a97f69-7fa0-4301-b498-128d78860828";

			final ResponseEntity<ReservationDto> response = ReservationControllerIT.this.restTemplate.exchange(
					MAPPING + FIND_BY_ID_PATH, HttpMethod.GET, RequestUtils.buildRequest(null, null),
					ReservationDto.class, reservationId);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			final ReservationDto answer = response.getBody();
			Assertions.assertThat(answer).as("reservation").isNotNull();
		}

		@Test
		void when_reservation_not_exists_should_return_not_found() throws Exception {
			final String reservationId = UUID.randomUUID().toString();

			final ResponseEntity<ReservationDto> response = ReservationControllerIT.this.restTemplate.exchange(
					MAPPING + FIND_BY_ID_PATH, HttpMethod.GET, RequestUtils.buildRequest(null, null),
					ReservationDto.class, reservationId);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		}
	}

	@Nested
	class Search {

		@Test
		@Sql({"/sql/reservation/single.sql"})
		void when_reservations_exists_filter_and_should_return_it() throws Exception {
			final String filter = "id:'d7a97f69-7fa0-4301-b498-128d78860828'";
			final CriteriaRequestDto criteria = new CriteriaRequestDto().filters(filter).page(0).limit(10);

			final ResponseEntity<ReservationPaginationResponse> response = ReservationControllerIT.this.restTemplate
					.exchange(MAPPING + SEARCH_PATH, HttpMethod.POST, RequestUtils.buildRequest(null, criteria),
							ReservationPaginationResponse.class, criteria);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			final ReservationPaginationResponse answer = response.getBody();
			Assertions.assertThat(answer).as("reservations").isNotNull();
		}

		@Test
		@Sql({"/sql/reservation/single.sql"})
		void when_reservations_exists_filter_with_sort_and_should_return_it() throws Exception {
			final String filter = "id:'d7a97f69-7fa0-4301-b498-128d78860828'";
			final CriteriaRequestDto criteria = new CriteriaRequestDto().filters(filter).page(0).limit(10).sortBy("id")
					.sortDirection("ASC");

			final ResponseEntity<ReservationPaginationResponse> response = ReservationControllerIT.this.restTemplate
					.exchange(MAPPING + SEARCH_PATH, HttpMethod.POST, RequestUtils.buildRequest(null, criteria),
							ReservationPaginationResponse.class, criteria);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			final var answer = response.getBody();
			Assertions.assertThat(answer).as("reservations").isNotNull();
			Assertions.assertThat(answer.getData()).as("reservations").hasSize(1);

		}

		@Test
		void when_reservations_not_exists_filter_with_sort_and_should_return_empty() throws Exception {
			final String filter = "id:'11a97f69-7fa0-4301-b498-128d78860828'";
			final CriteriaRequestDto criteria = new CriteriaRequestDto().filters(filter).page(0).limit(10).sortBy("id")
					.sortDirection("ASC");

			final ResponseEntity<ReservationPaginationResponse> response = ReservationControllerIT.this.restTemplate
					.exchange(MAPPING + SEARCH_PATH, HttpMethod.POST, RequestUtils.buildRequest(null, criteria),
							ReservationPaginationResponse.class, criteria);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			final var answer = response.getBody();
			Assertions.assertThat(answer).as("reservations").isNotNull();
			Assertions.assertThat(answer.getData()).as("reservations").isEmpty();
		}
	}

	@Nested
	class SearchAudit {

		@Test
		void when_reservation_has_audit_return_it() throws Exception {
			final UUID reservationId = UUID.randomUUID();
			final AuditFilters filters = AuditFilters.builder().id(reservationId).build();

			final int limit = 10;
			final ResponseEntity<ReservationListResponse> response = ReservationControllerIT.this.restTemplate.exchange(
					MAPPING + SEARCH_AUDIT_PATH, HttpMethod.POST, RequestUtils.buildRequest(null, filters),
					ReservationListResponse.class, limit);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			final ReservationListResponse answer = response.getBody();
			Assertions.assertThat(answer).as("reservations").isNotNull();
		}
	}

	@Nested
	class Create {

		@Test
		@Sql({"/sql/roomTypeInventory/single.sql"})
		void when_reservation_should_create_it() throws Exception {
			final UUID reservationId = UUID.randomUUID();
			final ReservationDto reservationDTO = new ReservationDto().id(reservationId)
					.hotelId(UUID.fromString("a1a97f69-7fa0-4301-b498-128d78860828")).roomTypeId(1)
					.guestId(UUID.randomUUID()).start(LocalDate.of(2024, 1, 30)).end(LocalDate.of(2024, 1, 30))
					.status("ON");

			final ResponseEntity<ReservationDto> response = ReservationControllerIT.this.restTemplate.exchange(MAPPING,
					HttpMethod.POST, RequestUtils.buildRequest(null, reservationDTO), ReservationDto.class);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

			final ReservationDto answer = response.getBody();
			Assertions.assertThat(answer).as("reservation").isNotNull();
			await().atMost(1, TimeUnit.SECONDS)
					.until(() -> ReservationControllerIT.this.outboxDomainRepository
							.findByAggregateId(answer.getId().toString()).stream()
							.allMatch(outboxEntity -> outboxEntity.getPublishedAt() != null));
			await().atMost(1, TimeUnit.SECONDS)
					.until(() -> ReservationControllerIT.this.outboxSnapshotRepository
							.findByAggregateId(answer.getId().toString()).stream()
							.allMatch(outboxEntity -> outboxEntity.getPublishedAt() != null));
		}

		@Test
		void when_reservation_incorrect_data_should_not_create_it() throws Exception {
			final UUID reservationId = UUID.randomUUID();
			final ReservationDto reservationDTO = new ReservationDto().id(reservationId).hotelId(UUID.randomUUID())
					.roomTypeId(1).guestId(UUID.randomUUID()).start(LocalDate.now()).end(LocalDate.now().minusDays(2))
					.status("ON_1234567890_1234567890");

			final ResponseEntity<ProblemDetail> response = ReservationControllerIT.this.restTemplate.exchange(MAPPING,
					HttpMethod.POST, RequestUtils.buildRequest(null, reservationDTO), ProblemDetail.class);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

			final ProblemDetail answer = response.getBody();
			Assertions.assertThat(answer).as("problemDetail").isNotNull();
			Assertions.assertThat(answer.getDetail()).as("detail").isNotNull();
		}

		@Test
		@Sql({"/sql/reservation/single.sql"})
		void when_reservation_already_exist_should_return_conflict() {
			final UUID reservationId = UUID.fromString("d7a97f69-7fa0-4301-b498-128d78860828");
			final ReservationDto reservationDTO = new ReservationDto().id(reservationId).hotelId(UUID.randomUUID())
					.roomTypeId(1).guestId(UUID.randomUUID()).start(LocalDate.now()).end(LocalDate.now()).status("ON");

			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PROBLEM_JSON));

			final HttpEntity<?> request = new HttpEntity<>(reservationDTO, headers);

			final ResponseEntity<ProblemDetail> response = ReservationControllerIT.this.restTemplate.exchange(MAPPING,
					HttpMethod.POST, request, ProblemDetail.class);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

		}

		@Test
		@Sql({"/sql/reservation/single.sql"})
		void when_reservation_has_not_availability_should_return_conflict() {
			final UUID reservationId = UUID.fromString("d7b97f69-7fa0-4301-b498-128d78860828");
			final ReservationDto reservationDTO = new ReservationDto().id(reservationId)
					.hotelId(UUID.fromString("a1a97f69-7fa0-4301-b498-128d78860828")).roomTypeId(1)
					.guestId(UUID.randomUUID()).start(LocalDate.of(2024, 1, 28)).end(LocalDate.of(2024, 1, 30))
					.status("ON");

			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PROBLEM_JSON));

			final HttpEntity<?> request = new HttpEntity<>(reservationDTO, headers);

			final ResponseEntity<ProblemDetail> response = ReservationControllerIT.this.restTemplate.exchange(MAPPING,
					HttpMethod.POST, request, ProblemDetail.class);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

		}
	}

	@Nested
	class Update {

		@Test
		void when_reservation_not_exists_should_not_found_exception() throws Exception {
			final UUID reservationId = UUID.randomUUID();
			final ReservationDto reservationDTO = new ReservationDto().id(reservationId).hotelId(UUID.randomUUID())
					.roomTypeId(4).guestId(UUID.randomUUID()).start(LocalDate.now()).end(LocalDate.now()).status("ON");

			final ResponseEntity<ReservationDto> response = ReservationControllerIT.this.restTemplate.exchange(MAPPING,
					HttpMethod.PUT, RequestUtils.buildRequest(null, reservationDTO), ReservationDto.class);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		}

		@Test
		@Sql({"/sql/reservation/single.sql"})
		@Sql({"/sql/roomTypeInventory/single.sql"})
		void when_reservation_exists_should_update_it() throws Exception {
			final UUID reservationId = UUID.fromString("d7a97f69-7fa0-4301-b498-128d78860828");
			final ReservationDto reservationDTO = new ReservationDto().id(reservationId)
					.hotelId(UUID.fromString("a1a97f69-7fa0-4301-b498-128d78860828")).roomTypeId(1)
					.guestId(UUID.randomUUID()).start(LocalDate.of(2024, 1, 30)).end(LocalDate.of(2024, 1, 30))
					.status("ON");

			final ResponseEntity<ReservationDto> response = ReservationControllerIT.this.restTemplate.exchange(MAPPING,
					HttpMethod.PUT, RequestUtils.buildRequest(null, reservationDTO), ReservationDto.class);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			final ReservationDto answer = response.getBody();
			Assertions.assertThat(answer).as("reservation").isNotNull();
			Assertions.assertThat(answer.getRoomTypeId()).as("roomTypeId").isEqualTo(1);
			Assertions.assertThat(answer.getStart()).as("start").isEqualTo(LocalDate.of(2024, 1, 30));
			Assertions.assertThat(answer.getEnd()).as("end").isEqualTo(LocalDate.of(2024, 1, 30));
		}
	}

	@Nested
	class Delete {

		@Test
		@Sql({"/sql/reservation/single.sql"})
		void when_reservation_exists_should_delete_it() throws Exception {
			final String reservationId = "d7a97f69-7fa0-4301-b498-128d78860828";

			final ResponseEntity<Void> response = ReservationControllerIT.this.restTemplate.exchange(
					MAPPING + DELETE_PATH, HttpMethod.DELETE, RequestUtils.buildRequest(null, null), Void.class,
					reservationId);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		}

		@Test
		void when_reservation_not_exists_and_delete_should_return_not_found() throws Exception {
			final String reservationId = UUID.randomUUID().toString();

			final ResponseEntity<Void> response = ReservationControllerIT.this.restTemplate.exchange(
					MAPPING + DELETE_PATH, HttpMethod.DELETE, RequestUtils.buildRequest(null, null), Void.class,
					reservationId);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		}
	}

}
