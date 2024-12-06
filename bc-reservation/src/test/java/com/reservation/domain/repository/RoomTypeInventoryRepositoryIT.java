package com.reservation.domain.repository;

import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.reservation.domain.model.RoomTypeInventory;
import com.reservation.utils.BaseITTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoomTypeInventoryRepositoryIT extends BaseITTest {

	@Autowired
	private RoomTypeInventoryRepository roomTypeInventoryRepository;

	@Nested
	class Create {

		@Test
		void when_roomTypeInventory_is_ok_should_create_it() {
			final UUID hotelId = UUID.randomUUID();
			final Integer roomTypeId = 1;
			final RoomTypeInventory roomTypeInventory = RoomTypeInventory.builder().id(UUID.randomUUID())
					.hotelId(hotelId).roomTypeId(roomTypeId).roomTypeInventoryDate(LocalDate.now()).totalInventory(1)
					.totalReserved(1).build();
			RoomTypeInventoryRepositoryIT.this.roomTypeInventoryRepository.save(roomTypeInventory);

			final Optional<RoomTypeInventory> optSearch = RoomTypeInventoryRepositoryIT.this.roomTypeInventoryRepository
					.findById(roomTypeInventory.getId().value());

			assertTrue(optSearch.isPresent());
			assertThat(optSearch.get().toString()).as("toString").isNotBlank();
			assertThat(optSearch.get().id()).as("id").isEqualTo(roomTypeInventory.id());
			assertThat(optSearch.get().id().toString()).as("aggregateId")
					.hasToString(roomTypeInventory.getAggregateId());
			assertThat(optSearch.get().hotelId()).as("hotelId").isEqualTo(hotelId);
			assertThat(optSearch.get().roomTypeId()).as("roomTypeId").isEqualTo(roomTypeId);
			assertThat(optSearch.get().totalInventory()).as("totalInventory")
					.isEqualTo(roomTypeInventory.totalInventory());
			assertThat(optSearch.get().totalReserved()).as("totalReserved")
					.isEqualTo(roomTypeInventory.totalReserved());
		}
	}

	@Nested
	class Search {

		@Test
		@Sql({"/sql/roomTypeInventory/single.sql"})
		void when_roomTypeInventory_filter_with_selection_should_return_it() {
			final String roomTypeInventoryId = "c1a97f69-7fa0-4301-b498-128d78860828";
			final String filter = String.format("id:'%s'", roomTypeInventoryId);
			final Criteria criteria = Criteria.builder().filters(filter).page(0).limit(10).sortBy("id")
					.sortDirection("ASC").build();

			final PaginationResponse<RoomTypeInventory> pageResponse = RoomTypeInventoryRepositoryIT.this.roomTypeInventoryRepository
					.searchBySelection(criteria);

			assertThat(pageResponse).as("pageResponse").isNotNull();
			assertThat(pageResponse.pagination()).as("pagination").isNotNull();
			assertThat(pageResponse.data()).as("data").isNotEmpty();
			assertThat(pageResponse.data().getFirst().getAggregateId()).as("aggregateId")
					.hasToString(roomTypeInventoryId);
			assertThat(pageResponse.data().getFirst().totalInventory()).as("totalInventory").isEqualTo(10);
			assertThat(pageResponse.data().getFirst().totalReserved()).as("totalReserved").isEqualTo(1);

		}

		@Test
		@Sql({"/sql/roomTypeInventory/single.sql"})
		void when_roomTypeInventory_filter_should_return_it() {
			final String roomTypeInventoryId = "c1a97f69-7fa0-4301-b498-128d78860828";
			final String filter = String.format("id:'%s'", roomTypeInventoryId);
			final Criteria criteria = Criteria.builder().filters(filter).page(0).limit(10).sortBy("id")
					.sortDirection("ASC").build();

			final PaginationResponse<RoomTypeInventory> pageResponse = RoomTypeInventoryRepositoryIT.this.roomTypeInventoryRepository
					.search(criteria);

			assertThat(pageResponse).as("pageResponse").isNotNull();
			assertThat(pageResponse.pagination()).as("pagination").isNotNull();
			assertThat(pageResponse.data()).as("data").isNotEmpty();
			assertThat(pageResponse.data().getFirst().getAggregateId()).as("aggregateId")
					.hasToString(roomTypeInventoryId);
			assertThat(pageResponse.data().getFirst().totalInventory()).as("totalInventory").isEqualTo(10);
			assertThat(pageResponse.data().getFirst().totalReserved()).as("totalReserved").isEqualTo(1);
		}
	}

	@Nested
	class SearchAudit {

		@Test
		@Transactional
		void when_roomTypeInventory_created_javers_audited_it_and_return_shadows() {
			final UUID id = UUID.randomUUID();
			final UUID hotelId = UUID.randomUUID();
			final Integer roomTypeId = 1;
			final RoomTypeInventory roomTypeInventory = RoomTypeInventory.builder().id(id).hotelId(hotelId)
					.roomTypeId(roomTypeId).roomTypeInventoryDate(LocalDate.now()).totalInventory(1).totalReserved(1)
					.build();
			RoomTypeInventoryRepositoryIT.this.roomTypeInventoryRepository.save(roomTypeInventory);

			final RoomTypeInventory roomTypeInventoryUpdate = RoomTypeInventory.builder().id(id).hotelId(hotelId)
					.roomTypeId(roomTypeId).roomTypeInventoryDate(LocalDate.now()).totalInventory(2).totalReserved(1)
					.build();
			RoomTypeInventoryRepositoryIT.this.roomTypeInventoryRepository.save(roomTypeInventoryUpdate);

			final RoomTypeInventory roomTypeInventoryUpdate2 = RoomTypeInventory.builder().id(id).hotelId(hotelId)
					.roomTypeId(roomTypeId).roomTypeInventoryDate(LocalDate.now()).totalInventory(2).totalReserved(2)
					.build();
			RoomTypeInventoryRepositoryIT.this.roomTypeInventoryRepository.save(roomTypeInventoryUpdate2);

			RoomTypeInventoryRepositoryIT.this.roomTypeInventoryRepository.delete(roomTypeInventory);

			final AuditFilters filters = AuditFilters.builder().id(id).from(LocalDateTime.now().minusYears(1))
					.to(LocalDateTime.now().plusDays(1)).build();
			final List<RoomTypeInventory> plannedProductList = RoomTypeInventoryRepositoryIT.this.roomTypeInventoryRepository
					.findAuditByFilters(filters, 10);

			assertThat(plannedProductList).hasSize(3);

		}
	}

}
