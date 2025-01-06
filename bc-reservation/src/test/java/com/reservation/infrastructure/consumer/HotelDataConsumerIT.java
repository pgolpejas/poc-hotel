package com.reservation.infrastructure.consumer;

import static com.hotel.core.domain.utils.EventHelper.EVENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.domain.avro.v1.HotelSnapshot;
import com.reservation.domain.dto.AggregatedReservationCriteria;
import com.reservation.domain.model.AggregatedReservation;
import com.reservation.domain.repository.AggregatedReservationRepository;
import com.reservation.infrastructure.repository.entity.HotelEntity;
import com.reservation.infrastructure.repository.entity.HotelEntity.RoomEntity;
import com.reservation.infrastructure.repository.redis.HotelRedisRepository;
import com.reservation.utils.BaseITTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.jdbc.Sql;

class HotelDataConsumerIT extends BaseITTest {

  @Autowired
  private HotelDataConsumer hotelDataConsumer;

  @Autowired
  private HotelRedisRepository hotelRedisRepository;

  @Autowired
  private AggregatedReservationRepository aggregatedReservationRepository;

  private static final UUID HOTEL_ID = UUID.fromString("d1a97f69-7fa0-4301-b498-128d78860828");

  @Test
  void when_hotel_not_created_and_not_reservation_by_hotel_id_then_add_hotel_in_redis() {

    this.cleanRedis();

    final HotelSnapshot hotelSnapshot = createHotelSnapshot();

    final Message<HotelSnapshot> message = createHotelSnapshotMessage(hotelSnapshot);

    this.hotelDataConsumer.handleHotel().accept(message);

    this.validateAddedHotelToHotelInRedis(hotelSnapshot);

    final PaginationResponse<AggregatedReservation> paginationResponse =
        this.getAggregatedReservationPaginationResponse(hotelSnapshot);
    assertThat(paginationResponse).isNotNull();
    assertThat(paginationResponse.data()).isEmpty();
  }

  @Test
  void when_hotel_created_and_not_reservation_by_hotel_id_then_update_hotel_in_redis() {

    this.cleanRedis();

    this.addHotelToRedis();

    final HotelSnapshot hotelSnapshot = createHotelSnapshot();

    final Message<HotelSnapshot> message = createHotelSnapshotMessage(hotelSnapshot);

    this.hotelDataConsumer.handleHotel().accept(message);

    this.validateAddedHotelToHotelInRedis(hotelSnapshot);

    final PaginationResponse<AggregatedReservation> paginationResponse =
        this.getAggregatedReservationPaginationResponse(hotelSnapshot);
    assertThat(paginationResponse).isNotNull();
    assertThat(paginationResponse.data()).isEmpty();
  }

  private void cleanRedis() {
    this.hotelRedisRepository.deleteAll();
    assertThat(this.hotelRedisRepository.findAll()).isEmpty();
  }

  private static HotelSnapshot createHotelSnapshot() {
    return HotelSnapshot.newBuilder()
        .setVersion(0)
        .setId(HOTEL_ID)
        .setName("Hotel 1")
        .setAddress("Address 1")
        .setCity("City 1")
        .setCountry("Country 1")
        .setPostalCode("Postal Code 1")
        .setState("State 1")
        .build();
  }

  @Test
  @Sql({"/sql/aggregated-reservation/single_without_hotel_data.sql"})
  void when_hotel_created_and_reservation_by_hotel_id_then_add_hotel_to_redis_and_update_reservation() {

    this.cleanRedis();

    this.addHotelToRedis();

    final HotelSnapshot hotelSnapshot = createHotelSnapshot();

    final Message<HotelSnapshot> message = createHotelSnapshotMessage(hotelSnapshot);

    this.hotelDataConsumer.handleHotel().accept(message);

    this.validateAddedHotelToHotelInRedis(hotelSnapshot);

    final PaginationResponse<AggregatedReservation> paginationResponse =
        this.getAggregatedReservationPaginationResponse(hotelSnapshot);
    assertThat(paginationResponse).isNotNull();
    assertThat(paginationResponse.data()).hasSize(1);
    assertThat(paginationResponse.data().getFirst().getReservation().hotel().rooms()).isEmpty();
  }

  @Test
  @Sql({"/sql/aggregated-reservation/single_without_hotel_data.sql"})
  void when_hotel_created_with_rooms_and_reservation_by_hotel_id_then_add_hotel_to_redis_and_update_reservation() {

    this.cleanRedis();

    this.hotelRedisRepository.save(HotelEntity.builder()
        .id(HOTEL_ID)
        .rooms(List.of(RoomEntity.builder()
            .id(UUID.randomUUID())
            .build()))
        .build());

    final HotelSnapshot hotelSnapshot = createHotelSnapshot();

    final Message<HotelSnapshot> message = createHotelSnapshotMessage(hotelSnapshot);

    this.hotelDataConsumer.handleHotel().accept(message);

    this.validateAddedHotelToHotelInRedis(hotelSnapshot);

    final PaginationResponse<AggregatedReservation> paginationResponse =
        this.getAggregatedReservationPaginationResponse(hotelSnapshot);
    assertThat(paginationResponse).isNotNull();
    assertThat(paginationResponse.data()).hasSize(1);
    assertThat(paginationResponse.data().getFirst().getReservation().hotel().rooms()).hasSize(1);
  }

  private void addHotelToRedis() {
    this.hotelRedisRepository.save(HotelEntity.builder()
        .id(HOTEL_ID)
        .build());
  }

  private PaginationResponse<AggregatedReservation> getAggregatedReservationPaginationResponse(HotelSnapshot hotelSnapshot) {
    return this.aggregatedReservationRepository.searchBySelection(AggregatedReservationCriteria.builder()
        .hotelIds(Set.of(hotelSnapshot.getId()))
        .page(0)
        .limit(-1)
        .sortDirection("ASC")
        .sortBy("start_date")
        .build());
  }

  private static @NotNull Message<HotelSnapshot> createHotelSnapshotMessage(HotelSnapshot hotelSnapshot) {
    final Map<String, Object> headers = new HashMap<>();
    headers.put(EVENT_TYPE, "HOTEL_SNAPSHOT");
    return MessageBuilder.withPayload(hotelSnapshot)
        .copyHeaders(headers)
        .build();
  }

  private void validateAddedHotelToHotelInRedis(HotelSnapshot hotelSnapshot) {
    final Optional<HotelEntity> optHotelEntity = this.hotelRedisRepository.findById(HOTEL_ID);

    assertThat(optHotelEntity).isPresent();

    final HotelEntity hotelEntity = optHotelEntity.get();
    assertThat(hotelEntity.getCity()).isEqualTo(hotelSnapshot.getCity());
    assertThat(hotelEntity.getCountry()).isEqualTo(hotelSnapshot.getCountry());
    assertThat(hotelEntity.getPostalCode()).isEqualTo(hotelSnapshot.getPostalCode());
    assertThat(hotelEntity.getAddress()).isEqualTo(hotelSnapshot.getAddress());
    assertThat(hotelEntity.getId()).isEqualTo(hotelSnapshot.getId());
    assertThat(hotelEntity.getName()).isEqualTo(hotelSnapshot.getName());
    assertThat(hotelEntity.getVersion()).isEqualTo(hotelSnapshot.getVersion());
    assertThat(hotelEntity.getState()).isEqualTo(hotelSnapshot.getState());
  }

}
