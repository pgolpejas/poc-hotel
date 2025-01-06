package com.reservation.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
public class Hotel implements Serializable {

  @Serial
  private static final long serialVersionUID = -7951604688458631729L;

  private UUID id;

  @NotBlank
  @Size(max = 100)
  private String name;

  @NotBlank
  @Size(max = 200)
  private String address;

  @NotBlank
  @Size(max = 100)
  private String city;

  @NotBlank
  @Size(max = 100)
  private String state;

  @NotBlank
  @Size(max = 100)
  private String country;

  @NotBlank
  @Size(max = 10)
  private String postalCode;

  private List<Room> rooms;

  @Builder
  public Hotel(@NotNull final UUID id,
      final int version,
      @NotBlank @Size(max = 100) final String name,
      @NotBlank @Size(max = 200) final String address,
      @NotBlank @Size(max = 100) final String city,
      @NotBlank @Size(max = 100) final String state,
      @NotBlank @Size(max = 100) final String country,
      @NotBlank @Size(max = 10) final String postalCode,
      final List<Room> rooms) {

    this.id = id;
    this.name = name;
    this.address = address;
    this.city = city;
    this.state = state;
    this.country = country;
    this.postalCode = postalCode;
    this.rooms = rooms == null ? new ArrayList<>() : new ArrayList<>(rooms);
  }

  public static Hotel create(final Hotel hotel) {
    return Hotel.builder()
        .id(hotel.id())
        .name(hotel.name())
        .address(hotel.address())
        .city(hotel.city())
        .state(hotel.state())
        .country(hotel.country())
        .postalCode(hotel.postalCode())
        .rooms(hotel.rooms())
        .build();
  }

  public void upsertRoom(final Room room) {
    if (this.rooms == null) {
      this.rooms = new ArrayList<>();
    }
    this.rooms.removeIf(existingRoom -> existingRoom.id().equals(room.id()));
    this.rooms.add(room);
  }

  public void updateHotel(Hotel hotelEntity) {
    this.name = hotelEntity.name;
    this.address = hotelEntity.address;
    this.city = hotelEntity.city;
    this.state = hotelEntity.state;
    this.country = hotelEntity.country;
    this.postalCode = hotelEntity.postalCode;
  }

  public UUID id() {
    return this.id;
  }

  public String name() {
    return this.name;
  }

  public String address() {
    return this.address;
  }

  public String city() {
    return this.city;
  }

  public String state() {
    return this.state;
  }

  public String country() {
    return this.country;
  }

  public String postalCode() {
    return this.postalCode;
  }

  public List<Room> rooms() {
    return new ArrayList<>(this.rooms);
  }

  public List<Room> getRooms() {
    return this.rooms();
  }

}
