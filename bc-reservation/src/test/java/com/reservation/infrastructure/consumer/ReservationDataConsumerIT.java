package com.reservation.infrastructure.consumer;

import static com.hotel.core.domain.utils.EventHelper.EVENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.hotel.core.domain.dto.PaginationResponse;
import com.reservation.domain.avro.v1.ReservationSnapshot;
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

class ReservationDataConsumerIT extends BaseITTest {

  @Autowired
  private ReservationDataConsumer reservationDataConsumer;

  @Autowired
  private HotelRedisRepository hotelRedisRepository;

  @Autowired
  private AggregatedReservationRepository aggregatedReservationRepository;

  private static final UUID HOTEL_ID = UUID.fromString("d1a97f69-7fa0-4301-b498-128d78860828");

  private static final UUID RESERVATION_ID = UUID.fromString("d1a97f69-7fa0-4301-b498-128d78860828");

  @Test
  void when_not_reservation_by_hotel_id_then_add_aggregated_reservation() {

    this.cleanRedis();

    final ReservationSnapshot reservationSnapshot = createReservationSnapshot();

    final Message<ReservationSnapshot> message = createReservationSnapshotMessage(reservationSnapshot);

    this.reservationDataConsumer.handleReservation().accept(message);

    final PaginationResponse<AggregatedReservation> paginationResponse =
        this.getAggregatedReservationPaginationResponse(reservationSnapshot);
    assertThat(paginationResponse).isNotNull();
    assertThat(paginationResponse.data()).hasSize(1);
    assertThat(paginationResponse.data().getFirst().getReservation().hotel().rooms()).isEmpty();
    assertThat(paginationResponse.data().getFirst().getReservation().hotel().id()).isEqualTo(HOTEL_ID);
    assertThat(paginationResponse.data().getFirst().getReservation().hotel().name()).isNull();
    assertThat(paginationResponse.data().getFirst().getReservation().start()).isEqualTo(LocalDate.now());
    assertThat(paginationResponse.data().getFirst().getReservation().end()).isEqualTo(LocalDate.now().plusDays(1));
    assertThat(paginationResponse.data().getFirst().getReservation().status()).isEqualTo("ON");
    assertThat(paginationResponse.data().getFirst().getReservation().roomTypeId()).isEqualTo(2);
    assertThat(paginationResponse.data().getFirst().getReservation().guestId()).isEqualTo(reservationSnapshot.getGuestId());
  }

  @Test
  void when_hotel_created_and_not_reservation_by_hotel_id_then_add_aggregated_reservation() {

    this.cleanRedis();

    this.addHotelToRedis();

    final ReservationSnapshot reservationSnapshot = createReservationSnapshot();

    final Message<ReservationSnapshot> message = createReservationSnapshotMessage(reservationSnapshot);

    this.reservationDataConsumer.handleReservation().accept(message);

    final PaginationResponse<AggregatedReservation> paginationResponse =
        this.getAggregatedReservationPaginationResponse(reservationSnapshot);
    assertThat(paginationResponse).isNotNull();
    assertThat(paginationResponse.data()).hasSize(1);
    assertThat(paginationResponse.data().getFirst().getReservation().hotel().rooms()).isEmpty();
    assertThat(paginationResponse.data().getFirst().getReservation().hotel().id()).isEqualTo(HOTEL_ID);
    assertThat(paginationResponse.data().getFirst().getReservation().hotel().name()).isEqualTo("Hotel");
    assertThat(paginationResponse.data().getFirst().getReservation().hotel().state()).isNull();
    assertThat(paginationResponse.data().getFirst().getReservation().start()).isEqualTo(LocalDate.now());
    assertThat(paginationResponse.data().getFirst().getReservation().end()).isEqualTo(LocalDate.now().plusDays(1));
    assertThat(paginationResponse.data().getFirst().getReservation().status()).isEqualTo("ON");
    assertThat(paginationResponse.data().getFirst().getReservation().roomTypeId()).isEqualTo(2);
    assertThat(paginationResponse.data().getFirst().getReservation().guestId()).isEqualTo(reservationSnapshot.getGuestId());
  }

  @Test
  void when_hotel_created_with_rooms_and_not_reservation_by_hotel_id_then_add_aggregated_reservation() {

    this.cleanRedis();

    this.hotelRedisRepository.save(HotelEntity.builder()
        .id(HOTEL_ID)
        .name("Hotel")
        .rooms(List.of(RoomEntity.builder()
            .id(UUID.randomUUID())
            .build()))
        .build());

    final ReservationSnapshot reservationSnapshot = createReservationSnapshot();

    final Message<ReservationSnapshot> message = createReservationSnapshotMessage(reservationSnapshot);

    this.reservationDataConsumer.handleReservation().accept(message);

    final PaginationResponse<AggregatedReservation> paginationResponse =
        this.getAggregatedReservationPaginationResponse(reservationSnapshot);
    assertThat(paginationResponse).isNotNull();
    assertThat(paginationResponse.data()).hasSize(1);
    assertThat(paginationResponse.data().getFirst().getReservation().hotel().rooms()).hasSize(1);
    assertThat(paginationResponse.data().getFirst().getReservation().hotel().id()).isEqualTo(HOTEL_ID);
    assertThat(paginationResponse.data().getFirst().getReservation().hotel().name()).isEqualTo("Hotel");
    assertThat(paginationResponse.data().getFirst().getReservation().start()).isEqualTo(LocalDate.now());
    assertThat(paginationResponse.data().getFirst().getReservation().end()).isEqualTo(LocalDate.now().plusDays(1));
    assertThat(paginationResponse.data().getFirst().getReservation().status()).isEqualTo("ON");
    assertThat(paginationResponse.data().getFirst().getReservation().roomTypeId()).isEqualTo(2);
    assertThat(paginationResponse.data().getFirst().getReservation().guestId()).isEqualTo(reservationSnapshot.getGuestId());
  }

  @Test
  @Sql({"/sql/aggregated-reservation/single_without_hotel_data.sql"})
  void when_hotel_created_with_rooms_and_reservation_by_hotel_id_then_add_hotel_to_redis_and_update_reservation() {

    this.cleanRedis();

    this.hotelRedisRepository.save(HotelEntity.builder()
        .id(HOTEL_ID)
        .name("Hotel")
        .rooms(List.of(RoomEntity.builder()
            .id(UUID.randomUUID())
            .build()))
        .build());

    final ReservationSnapshot reservationSnapshot = createReservationSnapshot();

    final Message<ReservationSnapshot> message = createReservationSnapshotMessage(reservationSnapshot);

    this.reservationDataConsumer.handleReservation().accept(message);

    final PaginationResponse<AggregatedReservation> paginationResponse =
        this.getAggregatedReservationPaginationResponse(reservationSnapshot);
    assertThat(paginationResponse).isNotNull();
    assertThat(paginationResponse.data()).hasSize(1);
    assertThat(paginationResponse.data().getFirst().getReservation().hotel().rooms()).hasSize(1);
    assertThat(paginationResponse.data().getFirst().getReservation().hotel().id()).isEqualTo(HOTEL_ID);
    assertThat(paginationResponse.data().getFirst().getReservation().hotel().name()).isEqualTo("Hotel");
    assertThat(paginationResponse.data().getFirst().getReservation().hotel().city()).isNull();
    assertThat(paginationResponse.data().getFirst().getReservation().start()).isEqualTo(LocalDate.now());
    assertThat(paginationResponse.data().getFirst().getReservation().end()).isEqualTo(LocalDate.now().plusDays(1));
    assertThat(paginationResponse.data().getFirst().getReservation().status()).isEqualTo("ON");
    assertThat(paginationResponse.data().getFirst().getReservation().roomTypeId()).isEqualTo(2);
    assertThat(paginationResponse.data().getFirst().getReservation().guestId()).isEqualTo(reservationSnapshot.getGuestId());
  }

  private void cleanRedis() {
    this.hotelRedisRepository.deleteAll();
    assertThat(this.hotelRedisRepository.findAll()).isEmpty();
  }

  private static ReservationSnapshot createReservationSnapshot() {
    return ReservationSnapshot.newBuilder()
        .setVersion(0)
        .setId(RESERVATION_ID)
        .setHotelId(HOTEL_ID)
        .setRoomTypeId(2)
        .setGuestId(UUID.randomUUID())
        .setStart(LocalDate.now().toString())
        .setEnd(LocalDate.now().plusDays(1).toString())
        .setStatus("ON")
        .build();
  }

  private void addHotelToRedis() {
    this.hotelRedisRepository.save(HotelEntity.builder()
        .id(HOTEL_ID)
        .name("Hotel")
        .city("City")
        .build());
  }

  private PaginationResponse<AggregatedReservation> getAggregatedReservationPaginationResponse(ReservationSnapshot reservationSnapshot) {
    return this.aggregatedReservationRepository.searchBySelection(AggregatedReservationCriteria.builder()
        .hotelIds(Set.of(reservationSnapshot.getHotelId()))
        .page(0)
        .limit(-1)
        .sortDirection("ASC")
        .sortBy("start_date")
        .build());
  }

  private static @NotNull Message<ReservationSnapshot> createReservationSnapshotMessage(ReservationSnapshot reservationSnapshot) {
    final Map<String, Object> headers = new HashMap<>();
    headers.put(EVENT_TYPE, "RESERVATION_SNAPSHOT");
    return MessageBuilder.withPayload(reservationSnapshot)
        .copyHeaders(headers)
        .build();
  }
}
