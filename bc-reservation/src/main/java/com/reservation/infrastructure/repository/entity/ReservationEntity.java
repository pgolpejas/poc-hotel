package com.reservation.infrastructure.repository.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "reservation")
@SuppressWarnings("squid:S1170")
public class ReservationEntity implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  private UUID id;

  @Version
  private int version;

  @NotNull
  @Column(name = "room_type_id")
  private Integer roomTypeId;

  @NotNull
  @Column(name = "hotel_id")
  private UUID hotelId;

  @NotNull
  @Column(name = "guest_id")
  private UUID guestId;

  @NotNull
  @Column(name = "start_date")
  private LocalDate start;

  @NotNull
  @Column(name = "end_date")
  private LocalDate end;

  @Column(name = "status")
  private String status;

  public ReservationEntity(UUID id, int version, Integer roomTypeId, UUID hotelId, UUID guestId, LocalDate start, LocalDate end) {
    this.id = id;
    this.version = version;
    this.roomTypeId = roomTypeId;
    this.hotelId = hotelId;
    this.guestId = guestId;
    this.start = start;
    this.end = end;
  }
}
