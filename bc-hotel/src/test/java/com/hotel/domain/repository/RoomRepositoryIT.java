package com.hotel.domain.repository;

import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.domain.model.Room;
import com.hotel.utils.BaseITTest;
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

class RoomRepositoryIT extends BaseITTest {

	private static final UUID HOTEL_ID = UUID.fromString("a1a97f69-7fa0-4301-b498-128d78860828");

	@Autowired
	private RoomRepository roomRepository;

	@Nested
	class Create {

		@Test
		@Sql({"/sql/hotel/single.sql"})
		void when_room_is_ok_should_create_it() {
			final Integer roomTypeId = 1;
			final Room room = Room.builder().id(UUID.randomUUID()).hotelId(HOTEL_ID).roomTypeId(roomTypeId).name("Room")
					.floor(1).roomNumber("101").available(true).build();
            RoomRepositoryIT.this.roomRepository.save(room);

			final Optional<Room> optSearch = RoomRepositoryIT.this.roomRepository.findById(room.getId().value());

			assertTrue(optSearch.isPresent());
			assertThat(optSearch.get().toString()).as("toString").isNotBlank();
			assertThat(optSearch.get().id()).as("id").isEqualTo(room.id());
			assertThat(optSearch.get().id().toString()).as("aggregateId").hasToString(room.getAggregateId());
			assertThat(optSearch.get().hotelId()).as("hotelId").isEqualTo(HOTEL_ID);
			assertThat(optSearch.get().roomTypeId()).as("roomTypeId").isEqualTo(roomTypeId);
			assertThat(optSearch.get().name()).as("name").isEqualTo(room.name());
			assertThat(optSearch.get().floor()).as("floor").isEqualTo(room.floor());
			assertThat(optSearch.get().roomNumber()).as("roomNumber").isEqualTo(room.roomNumber());
			assertThat(optSearch.get().available()).as("available").isEqualTo(room.available());
		}
	}

	@Nested
	class Search {

		@Test
		@Sql({"/sql/hotel/single.sql"})
		@Sql({"/sql/room/single.sql"})
		void when_room_filter_with_selection_should_return_it() {
			final String roomId = "d1b97f69-7fa0-4301-b498-128d78860828";
			final String filter = String.format("id:'%s'", roomId);
			final Criteria criteria = Criteria.builder().filters(filter).page(0).limit(10).sortBy("id")
					.sortDirection("ASC").build();

			final PaginationResponse<Room> pageResponse = RoomRepositoryIT.this.roomRepository.searchBySelection(criteria);

			assertThat(pageResponse).as("pageResponse").isNotNull();
			assertThat(pageResponse.pagination()).as("pagination").isNotNull();
			assertThat(pageResponse.data()).as("data").isNotEmpty();
			assertThat(pageResponse.data().getFirst().getAggregateId()).as("aggregateId").hasToString(roomId);
			assertThat(pageResponse.data().getFirst().name()).as("name").isEqualTo("ROOM1_1_1");
			assertThat(pageResponse.data().getFirst().floor()).as("floor").isEqualTo(1);
			assertThat(pageResponse.data().getFirst().roomNumber()).as("roomNumber").isEqualTo("1");

		}

		@Test
		@Sql({"/sql/hotel/single.sql"})
		@Sql({"/sql/room/single.sql"})
		void when_room_filter_should_return_it() {
			final String roomId = "d1b97f69-7fa0-4301-b498-128d78860828";
			final String filter = String.format("id:'%s'", roomId);
			final Criteria criteria = Criteria.builder().filters(filter).page(0).limit(10).sortBy("id")
					.sortDirection("ASC").build();

			final PaginationResponse<Room> pageResponse = RoomRepositoryIT.this.roomRepository.search(criteria);

			assertThat(pageResponse).as("pageResponse").isNotNull();
			assertThat(pageResponse.pagination()).as("pagination").isNotNull();
			assertThat(pageResponse.data()).as("data").isNotEmpty();
			assertThat(pageResponse.data().getFirst().getAggregateId()).as("aggregateId").hasToString(roomId);
			assertThat(pageResponse.data().getFirst().name()).as("name").isEqualTo("ROOM1_1_1");
			assertThat(pageResponse.data().getFirst().floor()).as("floor").isEqualTo(1);
			assertThat(pageResponse.data().getFirst().roomNumber()).as("roomNumber").isEqualTo("1");
		}
	}

	@Nested
	class SearchAudit {

		@Test
		@Transactional
		@Sql({"/sql/hotel/single.sql"})
		void when_room_created_javers_audited_it_and_return_shadows() {
			final UUID id = UUID.randomUUID();
			final Integer roomTypeId = 1;
			final Room room = Room.builder().id(id).hotelId(HOTEL_ID).roomTypeId(roomTypeId).name("Room").floor(1)
					.roomNumber("101").available(true).build();
            RoomRepositoryIT.this.roomRepository.save(room);

			final Room roomUpdate = Room.builder().id(id).hotelId(HOTEL_ID).roomTypeId(roomTypeId).name("Room1")
					.floor(1).roomNumber("102").available(true).build();
            RoomRepositoryIT.this.roomRepository.save(roomUpdate);

			final Room roomUpdate2 = Room.builder().id(id).hotelId(HOTEL_ID).roomTypeId(roomTypeId).name("Room31")
					.floor(12).roomNumber("1032").available(true).build();
            RoomRepositoryIT.this.roomRepository.save(roomUpdate2);

            RoomRepositoryIT.this.roomRepository.delete(room);

			final AuditFilters filters = AuditFilters.builder().id(id).from(LocalDateTime.now().minusYears(1))
					.to(LocalDateTime.now().plusDays(1)).build();
			final List<Room> plannedProductList = RoomRepositoryIT.this.roomRepository.findAuditByFilters(filters, 10);

			assertThat(plannedProductList).hasSize(3);

		}
	}

}
