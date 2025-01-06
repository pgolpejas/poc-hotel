package com.reservation.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings({"java:S107"})
public class Room implements Serializable {

  @Serial
  private static final long serialVersionUID = -7951604688458631729L;

  private UUID id;

  @NotNull
  private Integer roomTypeId;

  @Size(max = 100)
  private String name;

  private int floor;

  @NotBlank
  @Size(max = 5)
  private String roomNumber;

  private boolean available;

  @Builder
  public Room(@NotNull final UUID id,
      @NotNull final Integer roomTypeId,
      @Size(max = 100) final String name,
      @NotNull final int floor,
      @NotBlank @Size(max = 5) final String roomNumber,
      final boolean available) {

    this.id = id;
    this.roomTypeId = roomTypeId;
    this.name = name;
    this.floor = floor;
    this.roomNumber = roomNumber;
    this.available = available;
  }

  public static Room create(final Room room) {
    return Room.builder()
        .id(room.id())
        .roomTypeId(room.roomTypeId())
        .name(room.name())
        .floor(room.floor)
        .roomNumber(room.roomNumber())
        .available(room.available())
        .build();
  }

  public void update(final Room room) {
    this.name = room.name();
    this.floor = room.floor();
    this.roomNumber = room.roomNumber();
    this.available = room.available();
  }

  public UUID id() {
    return this.id;
  }

  public Integer roomTypeId() {
    return this.roomTypeId;
  }

  public String name() {
    return this.name;
  }

  public String roomNumber() {
    return this.roomNumber;
  }

  public int floor() {
    return this.floor;
  }

  public boolean available() {
    return this.available;
  }
}
