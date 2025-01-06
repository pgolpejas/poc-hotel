package com.reservation.infrastructure.repository.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.With;
import org.springframework.data.redis.core.RedisHash;

@Builder
@With
@RedisHash("Hotel")
@Data
public class HotelEntity implements Serializable {

  @Serial
  private static final long serialVersionUID = -7951604688458631729L;

  @Id
  private UUID id;

  private int version;

  private String name;

  private String address;

  private String city;

  private String state;

  private String country;

  private String postalCode;

  private List<RoomEntity> rooms;

  public void upsertRoom(final RoomEntity room) {
    if (this.rooms == null) {
      this.rooms = new ArrayList<>();
    }
    this.rooms.removeIf(existingRoom -> existingRoom.id().equals(room.id()));
    this.rooms.add(room);
  }

  public void updateHotel(HotelEntity hotelEntity) {
    this.version = hotelEntity.version;
    this.name = hotelEntity.name;
    this.address = hotelEntity.address;
    this.city = hotelEntity.city;
    this.state = hotelEntity.state;
    this.country = hotelEntity.country;
    this.postalCode = hotelEntity.postalCode;
  }

  @Builder
  @With
  public record RoomEntity(
      UUID id,

      int version,

      Integer roomTypeId,

      String name,

      int floor,

      String roomNumber,

      boolean available) implements Serializable {
  }
}
