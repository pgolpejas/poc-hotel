package com.hotel.application;

import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.openapi.model.CriteriaRequestDto;
import com.hotel.openapi.model.HotelDto;
import com.hotel.openapi.model.HotelListResponse;
import com.hotel.openapi.model.HotelPaginationResponse;
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

class HotelControllerIT extends BaseITTest {

	@Autowired
	protected TestRestTemplate restTemplate;

	private static final String DELIMITER_PATH = "/";

	private static final String MAPPING = DELIMITER_PATH + "v1/hotel";

	private static final String FIND_BY_ID_PATH = DELIMITER_PATH + "{id}";

	private static final String DELETE_PATH = DELIMITER_PATH + "{id}";

	private static final String SEARCH_PATH = DELIMITER_PATH + "search";

	private static final String SEARCH_AUDIT_PATH = DELIMITER_PATH + "search-audit/{limit}";

	@Nested
	class FindById {

		@Test
		@Sql({"/sql/hotel/single.sql"})
		void when_hotel_exists_should_return_it() throws Exception {
			final String hotelId = "a1a97f69-7fa0-4301-b498-128d78860828";

			final ResponseEntity<HotelDto> response = HotelControllerIT.this.restTemplate.exchange(
					MAPPING + FIND_BY_ID_PATH, HttpMethod.GET, RequestUtils.buildRequest(null, null), HotelDto.class,
					hotelId);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			final HotelDto answer = response.getBody();
			Assertions.assertThat(answer).as("hotel").isNotNull();
		}

		@Test
		void when_hotel_not_exists_should_return_not_found() throws Exception {
			final String hotelId = UUID.randomUUID().toString();

			final ResponseEntity<HotelDto> response = HotelControllerIT.this.restTemplate.exchange(
					MAPPING + FIND_BY_ID_PATH, HttpMethod.GET, RequestUtils.buildRequest(null, null), HotelDto.class,
					hotelId);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		}
	}

	@Nested
	class Search {

		@Test
		@Sql({"/sql/hotel/single.sql"})
		void when_hotels_exists_filter_and_should_return_it() throws Exception {
			final String filter = "id:'a1a97f69-7fa0-4301-b498-128d78860828'";
			final CriteriaRequestDto criteria = new CriteriaRequestDto().filters(filter).page(0).limit(10);

			final ResponseEntity<HotelPaginationResponse> response = HotelControllerIT.this.restTemplate.exchange(
					MAPPING + SEARCH_PATH, HttpMethod.POST, RequestUtils.buildRequest(null, criteria),
					HotelPaginationResponse.class, criteria);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			final HotelPaginationResponse answer = response.getBody();
			Assertions.assertThat(answer).as("hotels").isNotNull();
		}

		@Test
		@Sql({"/sql/hotel/single.sql"})
		void when_hotels_exists_filter_with_sort_and_should_return_it() throws Exception {
			final String filter = "id:'a1a97f69-7fa0-4301-b498-128d78860828'";
			final CriteriaRequestDto criteria = new CriteriaRequestDto().filters(filter).page(0).limit(10).sortBy("id")
					.sortDirection("ASC");

			final ResponseEntity<HotelPaginationResponse> response = HotelControllerIT.this.restTemplate.exchange(
					MAPPING + SEARCH_PATH, HttpMethod.POST, RequestUtils.buildRequest(null, criteria),
					HotelPaginationResponse.class, criteria);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			final var answer = response.getBody();
			Assertions.assertThat(answer).as("hotels").isNotNull();
			Assertions.assertThat(answer.getData()).as("hotels").hasSize(1);

		}

		@Test
		void when_hotels_not_exists_filter_with_sort_and_should_return_empty() throws Exception {
			final String filter = "id:'11a97f69-7fa0-4301-b498-128d78860828'";
			final CriteriaRequestDto criteria = new CriteriaRequestDto().filters(filter).page(0).limit(10).sortBy("id")
					.sortDirection("ASC");

			final ResponseEntity<HotelPaginationResponse> response = HotelControllerIT.this.restTemplate.exchange(
					MAPPING + SEARCH_PATH, HttpMethod.POST, RequestUtils.buildRequest(null, criteria),
					HotelPaginationResponse.class, criteria);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			final var answer = response.getBody();
			Assertions.assertThat(answer).as("hotels").isNotNull();
			Assertions.assertThat(answer.getData()).as("hotels").isEmpty();
		}
	}

	@Nested
	class SearchAudit {

		@Test
		void when_hotel_has_audit_return_it() throws Exception {
			final UUID hotelId = UUID.randomUUID();
			final AuditFilters filters = AuditFilters.builder().id(hotelId).build();

			final int limit = 10;
			final ResponseEntity<HotelListResponse> response = HotelControllerIT.this.restTemplate.exchange(
					MAPPING + SEARCH_AUDIT_PATH, HttpMethod.POST, RequestUtils.buildRequest(null, filters),
					HotelListResponse.class, limit);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			final HotelListResponse answer = response.getBody();
			Assertions.assertThat(answer).as("hotels").isNotNull();
		}
	}

	@Nested
	class Create {

		@Test
		void when_hotel_should_create_it() throws Exception {
			final UUID hotelId = UUID.randomUUID();
			final HotelDto hotelDTO = new HotelDto().id(hotelId).name("Hotel").address("Address")

					.city("City").state("State").country("Country").postalCode("12345");

			final ResponseEntity<HotelDto> response = HotelControllerIT.this.restTemplate.exchange(MAPPING,
					HttpMethod.POST, RequestUtils.buildRequest(null, hotelDTO), HotelDto.class);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

			final HotelDto answer = response.getBody();
			Assertions.assertThat(answer).as("hotel").isNotNull();
		}

		@Test
		void when_hotel_incorrect_data_should_not_create_it() throws Exception {
			final UUID hotelId = UUID.randomUUID();
			final HotelDto hotelDTO = new HotelDto().id(hotelId).name("Hotel").address("Address")

					.city("City").state("State").country("Country").postalCode("123452423424234");

			final ResponseEntity<ProblemDetail> response = HotelControllerIT.this.restTemplate.exchange(MAPPING,
					HttpMethod.POST, RequestUtils.buildRequest(null, hotelDTO), ProblemDetail.class);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

			final ProblemDetail answer = response.getBody();
			Assertions.assertThat(answer).as("problemDetail").isNotNull();
			Assertions.assertThat(answer.getDetail()).as("detail").isNotNull();
		}

		@Test
		@Sql({"/sql/hotel/single.sql"})
		void when_hotel_already_exist_should_return_conflict() {
			final UUID hotelId = UUID.fromString("a1a97f69-7fa0-4301-b498-128d78860828");
			final HotelDto hotelDTO = new HotelDto().id(hotelId).name("Hotel").address("Address")

					.city("City").state("State").country("Country").postalCode("12345");

			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PROBLEM_JSON));

			final HttpEntity<?> request = new HttpEntity<>(hotelDTO, headers);

			final ResponseEntity<ProblemDetail> response = HotelControllerIT.this.restTemplate.exchange(MAPPING,
					HttpMethod.POST, request, ProblemDetail.class);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

		}

	}

	@Nested
	class Update {

		@Test
		void when_hotel_not_exists_should_not_found_exception() throws Exception {
			final UUID hotelId = UUID.randomUUID();
			final HotelDto hotelDTO = new HotelDto().id(hotelId).name("Hotel").address("Address").city("City")
					.state("State").country("Country").postalCode("12345");

			final ResponseEntity<HotelDto> response = HotelControllerIT.this.restTemplate.exchange(MAPPING,
					HttpMethod.PUT, RequestUtils.buildRequest(null, hotelDTO), HotelDto.class);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		}

		@Test
		@Sql({"/sql/hotel/single.sql"})
		void when_hotel_exists_should_update_it() throws Exception {
			final UUID hotelId = UUID.fromString("a1a97f69-7fa0-4301-b498-128d78860828");
			final HotelDto hotelDTO = new HotelDto().id(hotelId).name("Hotel").address("Address")

					.city("City").state("State").country("Country").postalCode("12345");

			final ResponseEntity<HotelDto> response = HotelControllerIT.this.restTemplate.exchange(MAPPING,
					HttpMethod.PUT, RequestUtils.buildRequest(null, hotelDTO), HotelDto.class);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			final HotelDto answer = response.getBody();
			Assertions.assertThat(answer).as("hotel").isNotNull();
			Assertions.assertThat(answer.getName()).as("name").isEqualTo("Hotel");
			Assertions.assertThat(answer.getCity()).as("city").isEqualTo("City");
		}
	}

	@Nested
	class Delete {

		@Test
		@Sql({"/sql/hotel/single.sql"})
		void when_hotel_exists_should_delete_it() throws Exception {
			final String hotelId = "a1a97f69-7fa0-4301-b498-128d78860828";

			final ResponseEntity<Void> response = HotelControllerIT.this.restTemplate.exchange(MAPPING + DELETE_PATH,
					HttpMethod.DELETE, RequestUtils.buildRequest(null, null), Void.class, hotelId);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		}

		@Test
		void when_hotel_not_exists_and_delete_should_return_not_found() throws Exception {
			final String hotelId = UUID.randomUUID().toString();

			final ResponseEntity<Void> response = HotelControllerIT.this.restTemplate.exchange(MAPPING + DELETE_PATH,
					HttpMethod.DELETE, RequestUtils.buildRequest(null, null), Void.class, hotelId);

			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		}
	}

}
