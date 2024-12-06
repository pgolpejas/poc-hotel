package com.hotel.domain.repository;

import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.domain.model.Hotel;
import com.hotel.utils.BaseITTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HotelRepositoryIT extends BaseITTest {

	@Autowired
	private HotelRepository hotelRepository;

	@Nested
	class Create {

		@Test
		void when_hotel_is_ok_should_create_it() {
			final UUID hotelId = UUID.randomUUID();
			final Hotel hotel = Hotel.builder().id(hotelId).name("Hotel").address("Address").city("City").state("State")
					.country("Country").postalCode("12345").build();
            HotelRepositoryIT.this.hotelRepository.save(hotel);

			final Optional<Hotel> optSearch = HotelRepositoryIT.this.hotelRepository.findById(hotelId);

			assertTrue(optSearch.isPresent());
			Assertions.assertThat(optSearch.get().toString()).as("toString").isNotBlank();
			Assertions.assertThat(optSearch.get().id()).as("id").isEqualTo(hotelId);
			Assertions.assertThat(optSearch.get().id().toString()).as("aggregateId")
					.hasToString(hotel.getAggregateId());
			Assertions.assertThat(optSearch.get().name()).as("name").isEqualTo(hotel.name());
			Assertions.assertThat(optSearch.get().city()).as("city").isEqualTo(hotel.city());
			Assertions.assertThat(optSearch.get().state()).as("state").isEqualTo(hotel.state());
			Assertions.assertThat(optSearch.get().country()).as("country").isEqualTo(hotel.country());
			Assertions.assertThat(optSearch.get().postalCode()).as("postalCode").isEqualTo(hotel.postalCode());
		}
	}

	@Nested
	class Search {

		@Test
		@Sql({"/sql/hotel/single.sql"})
		void when_hotel_filter_with_selection_should_return_it() {
			final String hotelId = "a1a97f69-7fa0-4301-b498-128d78860828";
			final String filter = String.format("id:'%s'", hotelId);
			final Criteria criteria = Criteria.builder().filters(filter).page(0).limit(10).sortBy("id")
					.sortDirection("ASC").build();

			final PaginationResponse<Hotel> pageResponse = HotelRepositoryIT.this.hotelRepository.searchBySelection(criteria);

			Assertions.assertThat(pageResponse).as("pageResponse").isNotNull();
			Assertions.assertThat(pageResponse.pagination()).as("pagination").isNotNull();
			Assertions.assertThat(pageResponse.data()).as("data").isNotEmpty();
			Assertions.assertThat(pageResponse.data().getFirst().getAggregateId()).as("aggregateId")
					.hasToString(hotelId);
			Assertions.assertThat(pageResponse.data().getFirst().name()).as("name").isEqualTo("HOTEL_1");

		}

		@Test
		@Sql({"/sql/hotel/single.sql"})
		void when_hotel_filter_should_return_it() {
			final String hotelId = "a1a97f69-7fa0-4301-b498-128d78860828";
			final String filter = String.format("id:'%s'", hotelId);
			final Criteria criteria = Criteria.builder().filters(filter).page(0).limit(10).sortBy("id")
					.sortDirection("ASC").build();

			final PaginationResponse<Hotel> pageResponse = HotelRepositoryIT.this.hotelRepository.search(criteria);

			Assertions.assertThat(pageResponse).as("pageResponse").isNotNull();
			Assertions.assertThat(pageResponse.pagination()).as("pagination").isNotNull();
			Assertions.assertThat(pageResponse.data()).as("data").isNotEmpty();
			Assertions.assertThat(pageResponse.data().getFirst().getAggregateId()).as("aggregateId")
					.hasToString(hotelId);
		}
	}

	@Nested
	class SearchAudit {

		@Test
		@Transactional
		void when_hotel_created_javers_audited_it_and_return_shadows() {
			final UUID id = UUID.randomUUID();
			final Hotel hotel = Hotel.builder().id(id).name("Hotel").address("Address").city("City").state("State")
					.country("Country").postalCode("12345").build();
            HotelRepositoryIT.this.hotelRepository.save(hotel);

			final Hotel hotelUpdate = Hotel.builder().id(id).name("Hotel1").address("Address1").city("City11")
					.state("State1").country("Country1").postalCode("123451").build();
            HotelRepositoryIT.this.hotelRepository.save(hotelUpdate);

			final Hotel hotelUpdate2 = Hotel.builder().id(id).name("Hotel14").address("Address14").city("City114")
					.state("State14").country("Country144").postalCode("1234541").build();
            HotelRepositoryIT.this.hotelRepository.save(hotelUpdate2);

            HotelRepositoryIT.this.hotelRepository.delete(hotel);

			final AuditFilters filters = AuditFilters.builder().id(id).from(LocalDateTime.now().minusYears(1))
					.to(LocalDateTime.now().plusDays(1)).build();
			final List<Hotel> plannedProductList = HotelRepositoryIT.this.hotelRepository.findAuditByFilters(filters,
					10);

			assertThat(plannedProductList).hasSize(3);

		}
	}

}
