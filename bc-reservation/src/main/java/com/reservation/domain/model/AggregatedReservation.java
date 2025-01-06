package com.reservation.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

@Getter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings({"java:S107"})
public class AggregatedReservation implements Serializable {

  @Serial
  private static final long serialVersionUID = 761918126070437504L;

  private UUID id;

  private UUID hotelId;

  private LocalDate start;

  private LocalDate end;

  private Reservation reservation;

  @Builder
  public AggregatedReservation(final UUID id,
      final UUID hotelId,
      final LocalDate start,
      final LocalDate end,
      final Reservation reservation) {

    this.id = id;
    this.hotelId = hotelId;
    this.start = start;
    this.end = end;
    this.reservation = reservation;
  }

  public static AggregatedReservation create(final Reservation reservation) {
    return AggregatedReservation.builder()
        .id(reservation.id())
        .hotelId(reservation.hotel().id())
        .start(reservation.start())
        .end(reservation.end())
        .reservation(reservation).build();
  }

  public void updateReservation(final Reservation reservation) {
    this.hotelId = reservation.hotel().id();
    this.start = reservation.start();
    this.end = reservation.end();
    this.reservation = reservation;
  }

  public void updateHotel(final Hotel hotel) {
    this.reservation = this.reservation.withHotel(hotel);
  }

  @Builder
  @With
  public record Reservation(
      UUID id,

      Integer roomTypeId,

      Hotel hotel,

      UUID guestId,

      LocalDate start,

      LocalDate end,

      String status) implements Serializable {
  }
}
