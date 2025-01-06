package com.reservation.infrastructure.consumer;

import static com.hotel.core.domain.utils.EventHelper.EVENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.hotel.core.domain.dto.PaginationResponse;
import com.reservation.domain.dto.AggregatedReservationCriteria;
import com.reservation.domain.model.AggregatedReservation;
import com.reservation.domain.model.Room;
import com.reservation.domain.repository.AggregatedReservationRepository;
import com.reservation.infrastructure.repository.entity.HotelEntity;
import com.reservation.infrastructure.repository.entity.HotelEntity.RoomEntity;
import com.reservation.infrastructure.repository.redis.HotelRedisRepository;
import com.reservation.utils.BaseITTest;
import com.room.domain.avro.v1.RoomSnapshot;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.jdbc.Sql;

class RoomDataConsumerIT extends BaseITTest {

  @Autowired
  private RoomDataConsumer roomDataConsumer;

  @Autowired
  private HotelRedisRepository hotelRedisRepository;

  @Autowired
  private AggregatedReservationRepository aggregatedReservationRepository;

  private static final UUID HOTEL_ID = UUID.fromString("d1a97f69-7fa0-4301-b498-128d78860828");

  private static final UUID ROOM_ID = UUID.randomUUID();

  @Test
  void when_room_created_and_not_reservation_by_hotel_id_then_nothing() {

    this.cleanRedis();

    final RoomSnapshot roomSnapshot = createRoomSnapshot();

    final Message<RoomSnapshot> message = createRoomSnapshotMessage(roomSnapshot);

    this.roomDataConsumer.handleRoom().accept(message);

    final Optional<HotelEntity> optHotelEntity = this.hotelRedisRepository.findById(HOTEL_ID);
    assertThat(optHotelEntity).isNotPresent();

    final PaginationResponse<AggregatedReservation> paginationResponse =
        this.getAggregatedReservationPaginationResponse(roomSnapshot);
    assertThat(paginationResponse).isNotNull();
    assertThat(paginationResponse.data()).isEmpty();
  }

  @Test
  void when_room_created_and_not_reservation_by_hotel_id_then_add_room_to_hotel_to_redis() {

    this.cleanRedis();

    this.addHotelToRedis();

    final RoomSnapshot roomSnapshot = createRoomSnapshot();

    final Message<RoomSnapshot> message = createRoomSnapshotMessage(roomSnapshot);

    this.roomDataConsumer.handleRoom().accept(message);

    this.validateAddedRoomToHotelInRedis(roomSnapshot);

    final PaginationResponse<AggregatedReservation> paginationResponse =
        this.getAggregatedReservationPaginationResponse(roomSnapshot);
    assertThat(paginationResponse).isNotNull();
    assertThat(paginationResponse.data()).isEmpty();
  }

  private void cleanRedis() {
    this.hotelRedisRepository.deleteAll();
    assertThat(this.hotelRedisRepository.findAll()).isEmpty();
  }

  private static RoomSnapshot createRoomSnapshot() {
    return RoomSnapshot.newBuilder()
        .setVersion(0)
        .setHotelId(HOTEL_ID)
        .setId(ROOM_ID)
        .setRoomTypeId(2)
        .setFloor(1)
        .setAvailable(true)
        .setName("Room 1")
        .setRoomNumber("101")
        .build();
  }

  @Test
  @Sql({"/sql/aggregated-reservation/single_without_hotel_data.sql"})
  void when_room_created_and_reservation_by_hotel_id_then_add_hotel_to_redis_and_update_reservation() {

    this.cleanRedis();

    this.addHotelToRedis();

    final RoomSnapshot roomSnapshot = createRoomSnapshot();

    final Message<RoomSnapshot> message = createRoomSnapshotMessage(roomSnapshot);

    this.roomDataConsumer.handleRoom().accept(message);

    this.validateAddedRoomToHotelInRedis(roomSnapshot);

    final PaginationResponse<AggregatedReservation> paginationResponse =
        this.getAggregatedReservationPaginationResponse(roomSnapshot);
    assertThat(paginationResponse).isNotNull();
    assertThat(paginationResponse.data()).hasSize(1);
    assertThat(paginationResponse.data().getFirst().getReservation().hotel().rooms()).hasSize(1);
    final Room room = paginationResponse.data().getFirst().getReservation().hotel().rooms().getFirst();
    assertThat(room.id()).isEqualTo(ROOM_ID);
    assertThat(room.available()).isEqualTo(roomSnapshot.getAvailable());
    assertThat(room.name()).isEqualTo(roomSnapshot.getName());
    assertThat(room.roomNumber()).isEqualTo(roomSnapshot.getRoomNumber());
    assertThat(room.roomTypeId()).isEqualTo(roomSnapshot.getRoomTypeId());
  }

  private void addHotelToRedis() {
    this.hotelRedisRepository.save(HotelEntity.builder()
        .id(HOTEL_ID)
        .build());
  }

  private PaginationResponse<AggregatedReservation> getAggregatedReservationPaginationResponse(RoomSnapshot roomSnapshot) {
    return this.aggregatedReservationRepository.searchBySelection(AggregatedReservationCriteria.builder()
        .hotelIds(Set.of(roomSnapshot.getHotelId()))
        .page(0)
        .limit(-1)
        .sortDirection("ASC")
        .sortBy("start_date")
        .build());
  }

  private static @NotNull Message<RoomSnapshot> createRoomSnapshotMessage(RoomSnapshot roomSnapshot) {
    final Map<String, Object> headers = new HashMap<>();
    headers.put(EVENT_TYPE, "ROOM_SNAPSHOT");
    return MessageBuilder.withPayload(roomSnapshot)
        .copyHeaders(headers)
        .build();
  }

  private void validateAddedRoomToHotelInRedis(RoomSnapshot roomSnapshot) {
    final Optional<HotelEntity> optHotelEntity = this.hotelRedisRepository.findById(HOTEL_ID);

    assertThat(optHotelEntity).isPresent();

    final HotelEntity hotelEntity = optHotelEntity.get();
    assertThat(hotelEntity.getRooms()).hasSize(1);

    final RoomEntity roomEntity = hotelEntity.getRooms().getFirst();
    assertThat(roomEntity.roomTypeId()).isEqualTo(roomSnapshot.getRoomTypeId());
    assertThat(roomEntity.id()).isEqualTo(roomSnapshot.getId());
    assertThat(roomEntity.available()).isEqualTo(roomSnapshot.getAvailable());
    assertThat(roomEntity.name()).isEqualTo(roomSnapshot.getName());
    assertThat(roomEntity.roomNumber()).isEqualTo(roomSnapshot.getRoomNumber());
    assertThat(roomEntity.version()).isEqualTo(roomSnapshot.getVersion());
  }

}
