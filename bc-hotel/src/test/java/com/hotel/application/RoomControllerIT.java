package com.hotel.application;

import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.openapi.model.CriteriaRequestDto;
import com.hotel.openapi.model.RoomDto;
import com.hotel.openapi.model.RoomListResponse;
import com.hotel.openapi.model.RoomPaginationResponse;
import com.hotel.utils.BaseITTest;
import com.hotel.utils.RequestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;
import java.util.UUID;

class RoomControllerIT extends BaseITTest {

	@Autowired
	protected TestRestTemplate restTemplate;

	private static final String DELIMITER_PATH = "/";

	private static final String MAPPING = DELIMITER_PATH + "v1/hotel-room";

	private static final String FIND_BY_ID_PATH = DELIMITER_PATH + "{id}";

	private static final String DELETE_PATH = DELIMITER_PATH + "{id}";

	private static final String SEARCH_PATH = DELIMITER_PATH + "search";

	private static final String SEARCH_AUDIT_PATH = DELIMITER_PATH + "search-audit/{limit}";

	private static final UUID HOTEL_ID = UUID.fromString("a1a97f69-7fa0-4301-b498-128d78860828");

	@Nested
	class FindById {

		@Test
		@Sql({"/sql/hotel/single.sql"})
		@Sql({"/sql/room/single.sql"})
		void when_room_exists_should_return_it() {
			final String roomId = "d1b97f69-7fa0-4301-b498-128d78860828";

			final ResponseEntity<RoomDto> response = RoomControllerIT.this.restTemplate.exchange(
					MAPPING + FIND_BY_ID_PATH, HttpMethod.GET, RequestUtils.buildRequest(null, null), RoomDto.class,
					roomId);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			final RoomDto answer = response.getBody();
			Assertions.assertThat(answer).as("room").isNotNull();
		}

		@Test
		void when_room_not_exists_should_return_not_found() {
			final String roomId = UUID.randomUUID().toString();

			final ResponseEntity<RoomDto> response = RoomControllerIT.this.restTemplate.exchange(
					MAPPING + FIND_BY_ID_PATH, HttpMethod.GET, RequestUtils.buildRequest(null, null), RoomDto.class,
					roomId);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		}
	}

	@Nested
	class Search {

		@Test
		@Sql({"/sql/hotel/single.sql"})
		@Sql({"/sql/room/single.sql"})
		void when_roomTypeInventories_exists_filter_and_should_return_it() {
			final String filter = "id:'d1b97f69-7fa0-4301-b498-128d78860828'";
			final CriteriaRequestDto criteria = new CriteriaRequestDto().filters(filter).page(0).limit(10);

			final ResponseEntity<RoomPaginationResponse> response = RoomControllerIT.this.restTemplate.exchange(
					MAPPING + SEARCH_PATH, HttpMethod.POST, RequestUtils.buildRequest(null, criteria),
					RoomPaginationResponse.class, criteria);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			final RoomPaginationResponse answer = response.getBody();
			Assertions.assertThat(answer).as("roomTypeInventories").isNotNull();
		}

		@Test
		@Sql({"/sql/hotel/single.sql"})
		@Sql({"/sql/room/single.sql"})
		void when_roomTypeInventories_exists_filter_with_sort_and_should_return_it() {
			final String filter = "id:'d1b97f69-7fa0-4301-b498-128d78860828'";
			final CriteriaRequestDto criteria = new CriteriaRequestDto().filters(filter).page(0).limit(10).sortBy("id")
					.sortDirection("ASC");

			final ResponseEntity<RoomPaginationResponse> response = RoomControllerIT.this.restTemplate.exchange(
					MAPPING + SEARCH_PATH, HttpMethod.POST, RequestUtils.buildRequest(null, criteria),
					RoomPaginationResponse.class, criteria);

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

			final ResponseEntity<RoomPaginationResponse> response = RoomControllerIT.this.restTemplate.exchange(
					MAPPING + SEARCH_PATH, HttpMethod.POST, RequestUtils.buildRequest(null, criteria),
					RoomPaginationResponse.class, criteria);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			final var answer = response.getBody();
			Assertions.assertThat(answer).as("roomTypeInventories").isNotNull();
			Assertions.assertThat(answer.getData()).as("roomTypeInventories").isEmpty();
		}
	}

	@Nested
	class SearchAudit {

		@Test
		void when_room_has_audit_return_it() {
			final UUID roomId = UUID.randomUUID();
			final AuditFilters filters = AuditFilters.builder().id(roomId).build();

			final int limit = 10;
			final ResponseEntity<RoomListResponse> response = RoomControllerIT.this.restTemplate.exchange(
					MAPPING + SEARCH_AUDIT_PATH, HttpMethod.POST, RequestUtils.buildRequest(null, filters),
					RoomListResponse.class, limit);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			final RoomListResponse answer = response.getBody();
			Assertions.assertThat(answer).as("roomTypeInventories").isNotNull();
		}
	}

	@Nested
	class Create {

		@Test
		@Sql({"/sql/hotel/single.sql"})
		void when_room_should_create_it() {
			final UUID roomId = UUID.randomUUID();
			final RoomDto roomDTO = new RoomDto().id(roomId).hotelId(HOTEL_ID).roomTypeId(1).name("Room").floor(1)
					.roomNumber("101").available(true);

			final ResponseEntity<RoomDto> response = RoomControllerIT.this.restTemplate.exchange(MAPPING,
					HttpMethod.POST, RequestUtils.buildRequest(null, roomDTO), RoomDto.class);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

			final RoomDto answer = response.getBody();
			Assertions.assertThat(answer).as("room").isNotNull();
		}

		@Test
		@Sql({"/sql/hotel/single.sql"})
		void when_room_incorrect_data_should_not_create_it() {
			final UUID roomId = UUID.randomUUID();
			final RoomDto roomDTO = new RoomDto().id(roomId).hotelId(HOTEL_ID).roomTypeId(1).name("HOTEL_1").floor(1)
					.roomNumber("10123423423").available(true);

			final ResponseEntity<ProblemDetail> response = RoomControllerIT.this.restTemplate.exchange(MAPPING,
					HttpMethod.POST, RequestUtils.buildRequest(null, roomDTO), ProblemDetail.class);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

			final ProblemDetail answer = response.getBody();
			Assertions.assertThat(answer).as("problemDetail").isNotNull();
			Assertions.assertThat(answer.getDetail()).as("detail").isNotNull();
		}

		@Test
		@Sql({"/sql/hotel/single.sql"})
		@Sql({"/sql/room/single.sql"})
		void when_room_already_exist_by_id_should_return_conflict() {
			final UUID roomId = UUID.fromString("d1b97f69-7fa0-4301-b498-128d78860828");
			final RoomDto roomDTO = new RoomDto().id(roomId).hotelId(HOTEL_ID).roomTypeId(1).name("Room").floor(1)
					.roomNumber("101").available(true);

			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PROBLEM_JSON));

			final HttpEntity<?> request = new HttpEntity<>(roomDTO, headers);

			final ResponseEntity<ProblemDetail> response = RoomControllerIT.this.restTemplate.exchange(MAPPING,
					HttpMethod.POST, request, ProblemDetail.class);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

		}

		@Test
		@Sql({"/sql/hotel/single.sql"})
		@Sql({"/sql/room/single.sql"})
		void when_room_already_exist_by_uk_should_return_conflict() {
			final RoomDto roomDTO = new RoomDto().id(UUID.randomUUID()).hotelId(HOTEL_ID).roomTypeId(2)
					.name("ROOM1_1_1").floor(1).roomNumber("1").available(true);

			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PROBLEM_JSON));

			final HttpEntity<?> request = new HttpEntity<>(roomDTO, headers);

			final ResponseEntity<ProblemDetail> response = RoomControllerIT.this.restTemplate.exchange(MAPPING,
					HttpMethod.POST, request, ProblemDetail.class);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

		}
	}

	@Nested
	class Update {

		@Test
		void when_room_not_exists_should_not_found_exception() {
			final UUID roomId = UUID.randomUUID();
			final RoomDto roomDTO = new RoomDto().id(roomId).hotelId(HOTEL_ID).roomTypeId(4).name("Room").floor(1)
					.roomNumber("101").available(true);

			final ResponseEntity<RoomDto> response = RoomControllerIT.this.restTemplate.exchange(MAPPING,
					HttpMethod.PUT, RequestUtils.buildRequest(null, roomDTO), RoomDto.class);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		}

		@Test
		@Sql({"/sql/hotel/single.sql"})
		@Sql({"/sql/room/single.sql"})
		void when_room_exists_should_update_it() {
			final UUID roomId = UUID.fromString("d1b97f69-7fa0-4301-b498-128d78860828");
			final RoomDto roomDTO = new RoomDto().id(roomId).hotelId(HOTEL_ID).roomTypeId(4).name("Room").floor(1)
					.roomNumber("101").available(true);

			final ResponseEntity<RoomDto> response = RoomControllerIT.this.restTemplate.exchange(MAPPING,
					HttpMethod.PUT, RequestUtils.buildRequest(null, roomDTO), RoomDto.class);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			final RoomDto answer = response.getBody();
			Assertions.assertThat(answer).as("room").isNotNull();
			Assertions.assertThat(answer.getRoomTypeId()).as("roomTypeId").isEqualTo(4);
		}
	}

	@Nested
	class DeleteIT {

		@Test
		@Sql({"/sql/hotel/single.sql"})
		@Sql({"/sql/room/single.sql"})
		void when_room_exists_should_delete_it() {
			final String roomId = "d1b97f69-7fa0-4301-b498-128d78860828";

			final ResponseEntity<Void> response = RoomControllerIT.this.restTemplate.exchange(MAPPING + DELETE_PATH,
					HttpMethod.DELETE, RequestUtils.buildRequest(null, null), Void.class, roomId);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		}

		@Test
		void when_room_not_exists_and_delete_should_return_not_found() {
			final String roomId = UUID.randomUUID().toString();

			final ResponseEntity<Void> response = RoomControllerIT.this.restTemplate.exchange(MAPPING + DELETE_PATH,
					HttpMethod.DELETE, RequestUtils.buildRequest(null, null), Void.class, roomId);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		}
	}

}
