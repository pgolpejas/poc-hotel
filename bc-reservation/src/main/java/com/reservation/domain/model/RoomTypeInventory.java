package com.reservation.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

import com.hotel.core.domain.ddd.AggregateRoot;
import com.hotel.core.domain.ddd.DomainError;
import com.hotel.core.domain.utils.ValidationUtils;
import com.reservation.domain.event.RoomTypeInventoryDomainEvent.RoomTypeInventoryCreatedEvent;
import com.reservation.domain.event.RoomTypeInventoryDomainEvent.RoomTypeInventoryDeletedEvent;
import com.reservation.domain.event.RoomTypeInventoryDomainEvent.RoomTypeInventoryUpdatedEvent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings({"java:S107"})
public class RoomTypeInventory extends AggregateRoot implements Serializable {

  @Serial
  private static final long serialVersionUID = 7611233896070437504L;

  private RoomTypeInventoryId id;

  private int version;

  private Integer roomTypeId;

  private HotelId hotelId;

  private LocalDate roomTypeInventoryDate;

  @PositiveOrZero
  private long totalInventory;

  @PositiveOrZero
  private long totalReserved;

  @Builder
  public RoomTypeInventory(final UUID id,
      final int version,
      final Integer roomTypeId,
      final UUID hotelId,
      final LocalDate roomTypeInventoryDate,
      @PositiveOrZero final long totalInventory,
      @PositiveOrZero final long totalReserved) {

    this.id = new RoomTypeInventoryId(id);
    this.version = version;
    this.roomTypeId = roomTypeId;
    this.hotelId = new HotelId(hotelId);
    this.roomTypeInventoryDate = roomTypeInventoryDate;
    this.totalInventory = totalInventory;
    this.totalReserved = totalReserved;

    commonValidations(this);

  }

  public static RoomTypeInventory create(final RoomTypeInventory roomTypeInventory) {

    // Common domain validations to this action (include db validations)
    commonValidations(roomTypeInventory);

    final RoomTypeInventory newAggregate = RoomTypeInventory.builder()
        .id(roomTypeInventory.id())
        .hotelId(roomTypeInventory.hotelId())
        .roomTypeId(roomTypeInventory.roomTypeId())
        .roomTypeInventoryDate(roomTypeInventory.roomTypeInventoryDate())
        .totalInventory(roomTypeInventory.totalInventory())
        .totalReserved(roomTypeInventory.totalReserved())
        .build();

    newAggregate.registerEvent(
        RoomTypeInventoryCreatedEvent.builder()
            .id(newAggregate.id())
            .roomTypeId(newAggregate.getRoomTypeId())
            .hotelId(newAggregate.hotelId())
            .roomTypeInventoryDate(newAggregate.roomTypeInventoryDate())
            .totalInventory(newAggregate.totalInventory())
            .totalReserved(newAggregate.totalReserved())
            .build());

    return newAggregate;
  }

  private static void commonValidations(final RoomTypeInventory roomTypeInventory) {
    ValidationUtils.notNull(roomTypeInventory.roomTypeInventoryDate, "roomTypeInventoryDate can not be null");

    if (roomTypeInventory.totalInventory() < 0) {
      throw new DomainError("Total inventory can not be negative");
    }
    if (roomTypeInventory.totalReserved() < 0) {
      throw new DomainError("Total reserved can not be negative");
    }
  }

  public void delete() {
    this.registerEvent(RoomTypeInventoryDeletedEvent.builder()
        .id(this.id())
        .build());
  }

  public void update(final long totalInventory, final long totalReserved) {
    this.totalInventory = totalInventory;
    this.totalReserved = totalReserved;

    this.registerEvent(
        RoomTypeInventoryUpdatedEvent.builder()
            .id(this.id())
            .roomTypeId(this.getRoomTypeId())
            .hotelId(this.hotelId())
            .roomTypeInventoryDate(this.roomTypeInventoryDate())
            .totalInventory(this.totalInventory())
            .totalReserved(this.totalReserved())
            .build());
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

  public LocalDate roomTypeInventoryDate() {
    return this.roomTypeInventoryDate;
  }

  public long totalInventory() {
    return this.totalInventory;
  }

  public long totalReserved() {
    return this.totalReserved;
  }

}
