package com.reservation.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

import com.hotel.core.domain.ddd.AggregateRoot;
import com.hotel.core.domain.ddd.DomainError;
import com.hotel.core.domain.utils.ValidationUtils;
import com.reservation.domain.event.ReservationDomainEvent.ReservationCreatedEvent;
import com.reservation.domain.event.ReservationDomainEvent.ReservationDeletedEvent;
import com.reservation.domain.event.ReservationDomainEvent.ReservationUpdatedEvent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings({"java:S107"})
public class Reservation extends AggregateRoot implements Serializable {

  @Serial
  private static final long serialVersionUID = 7619183896070437504L;

  private ReservationId id;

  private int version;

  @NotNull
  private Integer roomTypeId;

  private HotelId hotelId;

  private GuestId guestId;

  @NotNull
  private LocalDate start;

  @NotNull
  private LocalDate end;

  @Size(max = 20)
  private String status;

  @Builder
  public Reservation(@NotNull final UUID id,
      final int version,
      @NotNull final Integer roomTypeId,
      @NotNull final UUID hotelId,
      @NotNull final UUID guestId,
      @NotNull final LocalDate start,
      @NotNull final LocalDate end,
      @Size(max = 20) final String status) {

    this.id = new ReservationId(id);
    this.version = version;
    this.roomTypeId = roomTypeId;
    this.hotelId = new HotelId(hotelId);
    this.guestId = new GuestId(guestId);
    this.start = start;
    this.end = end;
    this.status = status;

    // Common validations for all actions on the aggregate
    commonValidations(this);
  }

  public static Reservation create(final Reservation reservation) {

    // Common domain validations to this action (include db validations)
    commonValidations(reservation);

    final Reservation newAggregate = Reservation.builder()
        .id(reservation.id())
        .hotelId(reservation.hotelId())
        .roomTypeId(reservation.roomTypeId())
        .guestId(reservation.guestId())
        .start(reservation.start())
        .end(reservation.end())
        .status(reservation.status())
        .build();

    newAggregate.registerEvent(
        ReservationCreatedEvent.builder()
            .id(newAggregate.id())
            .roomTypeId(newAggregate.getRoomTypeId())
            .hotelId(newAggregate.hotelId())
            .guestId(newAggregate.guestId())
            .roomTypeId(newAggregate.getRoomTypeId())
            .start(newAggregate.start())
            .end(newAggregate.end())
            .status(newAggregate.getStatus())
            .build());

    return newAggregate;
  }

  public void delete() {
    this.registerEvent(ReservationDeletedEvent.builder()
        .id(this.id())
        .build());
  }

  public void update(final Reservation reservation) {
    this.hotelId = new HotelId(reservation.hotelId());
    this.guestId = new GuestId(reservation.guestId());
    this.roomTypeId = reservation.roomTypeId();
    this.start = reservation.start();
    this.end = reservation.end();
    this.status = reservation.status();

    this.registerEvent(
        ReservationUpdatedEvent.builder()
            .id(this.id())
            .roomTypeId(this.getRoomTypeId())
            .hotelId(this.hotelId())
            .guestId(this.guestId())
            .roomTypeId(this.getRoomTypeId())
            .start(this.start())
            .end(this.end())
            .status(this.getStatus())
            .build());
  }

  private static void commonValidations(final Reservation reservation) {
    ValidationUtils.notNull(reservation.start(), "start can not be null");
    ValidationUtils.notNull(reservation.end(), "end can not be null");
    ValidationUtils.maxSize(reservation.status(), 20, "status can not be greater than 20 characters");

    if (reservation.start().isAfter(reservation.end())) {
      throw new DomainError("Start date: %s cannot be later than end date: %s", reservation.start(), reservation.end());
    }
  }

  public UUID id() {
    return this.id.value();
  }

  @Override
  public String getAggregateId() {
    return this.id.value().toString();
  }

  public Integer roomTypeId() {
    return this.roomTypeId;
  }

  public UUID hotelId() {
    return this.hotelId.value();
  }

  public UUID guestId() {
    return this.guestId.value();
  }

  public LocalDate start() {
    return this.start;
  }

  public LocalDate end() {
    return this.end;
  }

  public String status() {
    return this.status;
  }

}
