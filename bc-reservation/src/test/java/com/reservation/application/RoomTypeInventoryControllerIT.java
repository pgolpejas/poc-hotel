package com.reservation.application;

import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.reservation.openapi.model.CriteriaRequestDto;
import com.reservation.openapi.model.RoomTypeInventoryDto;
import com.reservation.openapi.model.RoomTypeInventoryListResponse;
import com.reservation.openapi.model.RoomTypeInventoryPaginationResponse;
import com.reservation.utils.BaseITTest;
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

class RoomTypeInventoryControllerIT extends BaseITTest {

	@Autowired
	protected TestRestTemplate restTemplate;

	private static final String DELIMITER_PATH = "/";

	private static final String MAPPING = DELIMITER_PATH + "v1/hotel-room-type-inventory";

	private static final String FIND_BY_ID_PATH = DELIMITER_PATH + "{id}";

	private static final String DELETE_PATH = DELIMITER_PATH + "{id}";

	private static final String SEARCH_PATH = DELIMITER_PATH + "search";

	private static final String SEARCH_AUDIT_PATH = DELIMITER_PATH + "search-audit/{limit}";

	@Nested
	class FindById {

		@Test
		@Sql({"/sql/roomTypeInventory/single.sql"})
		void when_roomTypeInventory_exists_should_return_it() {
			final String roomTypeInventoryId = "c1a97f69-7fa0-4301-b498-128d78860828";

			final ResponseEntity<RoomTypeInventoryDto> response = RoomTypeInventoryControllerIT.this.restTemplate
					.exchange(MAPPING + FIND_BY_ID_PATH, HttpMethod.GET, RequestUtils.buildRequest(null, null),
							RoomTypeInventoryDto.class, roomTypeInventoryId);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			final RoomTypeInventoryDto answer = response.getBody();
			Assertions.assertThat(answer).as("roomTypeInventory").isNotNull();
		}

		@Test
		void when_roomTypeInventory_not_exists_should_return_not_found() {
			final String roomTypeInventoryId = UUID.randomUUID().toString();

			final ResponseEntity<RoomTypeInventoryDto> response = RoomTypeInventoryControllerIT.this.restTemplate
					.exchange(MAPPING + FIND_BY_ID_PATH, HttpMethod.GET, RequestUtils.buildRequest(null, null),
							RoomTypeInventoryDto.class, roomTypeInventoryId);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		}
	}

	@Nested
	class Search {

		@Test
		@Sql({"/sql/roomTypeInventory/single.sql"})
		void when_roomTypeInventories_exists_filter_and_should_return_it() {
			final String filter = "id:'c1a97f69-7fa0-4301-b498-128d78860828'";
			final CriteriaRequestDto criteria = new CriteriaRequestDto().filters(filter).page(0).limit(10);

			final ResponseEntity<RoomTypeInventoryPaginationResponse> response = RoomTypeInventoryControllerIT.this.restTemplate
					.exchange(MAPPING + SEARCH_PATH, HttpMethod.POST, RequestUtils.buildRequest(null, criteria),
							RoomTypeInventoryPaginationResponse.class, criteria);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			final RoomTypeInventoryPaginationResponse answer = response.getBody();
			Assertions.assertThat(answer).as("roomTypeInventories").isNotNull();
		}

		@Test
		@Sql({"/sql/roomTypeInventory/single.sql"})
		void when_roomTypeInventories_exists_filter_with_sort_and_should_return_it() {
			final String filter = "id:'c1a97f69-7fa0-4301-b498-128d78860828'";
			final CriteriaRequestDto criteria = new CriteriaRequestDto().filters(filter).page(0).limit(10).sortBy("id")
					.sortDirection("ASC");

			final ResponseEntity<RoomTypeInventoryPaginationResponse> response = RoomTypeInventoryControllerIT.this.restTemplate
					.exchange(MAPPING + SEARCH_PATH, HttpMethod.POST, RequestUtils.buildRequest(null, criteria),
							RoomTypeInventoryPaginationResponse.class, criteria);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			final var answer = response.getBody();
			Assertions.assertThat(answer).as("roomTypeInventories").isNotNull();
			Assertions.assertThat(answer.getData()).as("roomTypeInventories").hasSize(1);

		}

		@Test
		void when_roomTypeInventories_not_exists_filter_with_sort_and_should_return_empty() {
			final String filter = "id:'11a97f69-7fa0-4301-b498-128d78860828'";
			final CriteriaRequestDto criteria = new CriteriaRequestDto().filters(filter).page(0).limit(10).sortBy("id")
					.sortDirection("ASC");

			final ResponseEntity<RoomTypeInventoryPaginationResponse> response = RoomTypeInventoryControllerIT.this.restTemplate
					.exchange(MAPPING + SEARCH_PATH, HttpMethod.POST, RequestUtils.buildRequest(null, criteria),
							RoomTypeInventoryPaginationResponse.class, criteria);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			final var answer = response.getBody();
			Assertions.assertThat(answer).as("roomTypeInventories").isNotNull();
			Assertions.assertThat(answer.getData()).as("roomTypeInventories").isEmpty();
		}
	}

	@Nested
	class SearchAudit {

		@Test
		void when_roomTypeInventory_has_audit_return_it() {
			final UUID roomTypeInventoryId = UUID.randomUUID();
			final AuditFilters filters = AuditFilters.builder().id(roomTypeInventoryId).build();

			final int limit = 10;
			final ResponseEntity<RoomTypeInventoryListResponse> response = RoomTypeInventoryControllerIT.this.restTemplate
					.exchange(MAPPING + SEARCH_AUDIT_PATH, HttpMethod.POST, RequestUtils.buildRequest(null, filters),
							RoomTypeInventoryListResponse.class, limit);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			final RoomTypeInventoryListResponse answer = response.getBody();
			Assertions.assertThat(answer).as("roomTypeInventories").isNotNull();
		}
	}

	@Nested
	class Create {

		@Test
		void when_roomTypeInventory_should_create_it() {
			final UUID roomTypeInventoryId = UUID.randomUUID();
			final RoomTypeInventoryDto roomTypeInventoryDTO = new RoomTypeInventoryDto().id(roomTypeInventoryId)
					.hotelId(UUID.randomUUID()).roomTypeId(1).roomTypeInventoryDate(LocalDate.now()).totalInventory(10)
					.totalReserved(1);

			final ResponseEntity<RoomTypeInventoryDto> response = RoomTypeInventoryControllerIT.this.restTemplate
					.exchange(MAPPING, HttpMethod.POST, RequestUtils.buildRequest(null, roomTypeInventoryDTO),
							RoomTypeInventoryDto.class);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

			final RoomTypeInventoryDto answer = response.getBody();
			Assertions.assertThat(answer).as("roomTypeInventory").isNotNull();
		}

		@Test
		void when_roomTypeInventory_incorrect_data_should_not_create_it() {
			final UUID roomTypeInventoryId = UUID.randomUUID();
			final RoomTypeInventoryDto roomTypeInventoryDTO = new RoomTypeInventoryDto().id(roomTypeInventoryId)
					.hotelId(UUID.randomUUID()).roomTypeId(1).roomTypeInventoryDate(LocalDate.now()).totalInventory(-10)
					.totalReserved(1);

			final ResponseEntity<ProblemDetail> response = RoomTypeInventoryControllerIT.this.restTemplate.exchange(
					MAPPING, HttpMethod.POST, RequestUtils.buildRequest(null, roomTypeInventoryDTO),
					ProblemDetail.class);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

			final ProblemDetail answer = response.getBody();
			Assertions.assertThat(answer).as("problemDetail").isNotNull();
			Assertions.assertThat(answer.getDetail()).as("detail").isNotNull();
		}

		@Test
		@Sql({"/sql/roomTypeInventory/single.sql"})
		void when_roomTypeInventory_already_exist_by_id_should_return_conflict() {
			final UUID roomTypeInventoryId = UUID.fromString("c1a97f69-7fa0-4301-b498-128d78860828");
			final RoomTypeInventoryDto roomTypeInventoryDTO = new RoomTypeInventoryDto().id(roomTypeInventoryId)
					.hotelId(UUID.randomUUID()).roomTypeId(1).roomTypeInventoryDate(LocalDate.now()).totalInventory(10)
					.totalReserved(1);

			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PROBLEM_JSON));

			final HttpEntity<?> request = new HttpEntity<>(roomTypeInventoryDTO, headers);

			final ResponseEntity<ProblemDetail> response = RoomTypeInventoryControllerIT.this.restTemplate
					.exchange(MAPPING, HttpMethod.POST, request, ProblemDetail.class);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

		}

		@Test
		@Sql({"/sql/roomTypeInventory/single.sql"})
		void when_roomTypeInventory_already_exist_by_uk_should_return_conflict() {
			final RoomTypeInventoryDto roomTypeInventoryDTO = new RoomTypeInventoryDto().id(UUID.randomUUID())
					.hotelId(UUID.fromString("a1a97f69-7fa0-4301-b498-128d78860828")).roomTypeId(1)
					.roomTypeInventoryDate(LocalDate.of(2024, 1, 30)).totalInventory(10).totalReserved(1);

			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PROBLEM_JSON));

			final HttpEntity<?> request = new HttpEntity<>(roomTypeInventoryDTO, headers);

			final ResponseEntity<ProblemDetail> response = RoomTypeInventoryControllerIT.this.restTemplate
					.exchange(MAPPING, HttpMethod.POST, request, ProblemDetail.class);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

		}
	}

	@Nested
	class Update {

		@Test
		void when_roomTypeInventory_not_exists_should_not_found_exception() {
			final UUID roomTypeInventoryId = UUID.randomUUID();
			final RoomTypeInventoryDto roomTypeInventoryDTO = new RoomTypeInventoryDto().id(roomTypeInventoryId)
					.hotelId(UUID.randomUUID()).roomTypeId(4).roomTypeInventoryDate(LocalDate.now()).totalInventory(10)
					.totalReserved(1);

			final ResponseEntity<RoomTypeInventoryDto> response = RoomTypeInventoryControllerIT.this.restTemplate
					.exchange(MAPPING, HttpMethod.PUT, RequestUtils.buildRequest(null, roomTypeInventoryDTO),
							RoomTypeInventoryDto.class);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		}

		@Test
		@Sql({"/sql/roomTypeInventory/single.sql"})
		void when_roomTypeInventory_exists_should_update_it() {
			final UUID roomTypeInventoryId = UUID.fromString("c1a97f69-7fa0-4301-b498-128d78860828");
			final RoomTypeInventoryDto roomTypeInventoryDTO = new RoomTypeInventoryDto().id(roomTypeInventoryId)
					.hotelId(UUID.randomUUID()).roomTypeId(4).roomTypeInventoryDate(LocalDate.now()).totalInventory(10)
					.totalReserved(1);

			final ResponseEntity<RoomTypeInventoryDto> response = RoomTypeInventoryControllerIT.this.restTemplate
					.exchange(MAPPING, HttpMethod.PUT, RequestUtils.buildRequest(null, roomTypeInventoryDTO),
							RoomTypeInventoryDto.class);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			final RoomTypeInventoryDto answer = response.getBody();
			Assertions.assertThat(answer).as("roomTypeInventory").isNotNull();
			Assertions.assertThat(answer.getRoomTypeId()).as("roomTypeId").isEqualTo(4);
		}
	}

	@Nested
	class DeleteIT {

		@Test
		@Sql({"/sql/roomTypeInventory/single.sql"})
		void when_roomTypeInventory_exists_should_delete_it() {
			final String roomTypeInventoryId = "c1a97f69-7fa0-4301-b498-128d78860828";

			final ResponseEntity<Void> response = RoomTypeInventoryControllerIT.this.restTemplate.exchange(
					MAPPING + DELETE_PATH, HttpMethod.DELETE, RequestUtils.buildRequest(null, null), Void.class,
					roomTypeInventoryId);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		}

		@Test
		void when_roomTypeInventory_not_exists_and_delete_should_return_not_found() {
			final String roomTypeInventoryId = UUID.randomUUID().toString();

			final ResponseEntity<Void> response = RoomTypeInventoryControllerIT.this.restTemplate.exchange(
					MAPPING + DELETE_PATH, HttpMethod.DELETE, RequestUtils.buildRequest(null, null), Void.class,
					roomTypeInventoryId);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		}
	}

}
