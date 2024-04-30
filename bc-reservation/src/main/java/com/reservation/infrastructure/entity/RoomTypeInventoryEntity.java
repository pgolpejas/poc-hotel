package com.reservation.infrastructure.entity;

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
@Table(name = "room_type_inventory")
@SuppressWarnings("squid:S1170")
public class RoomTypeInventoryEntity implements Serializable {

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
  @Column(name = "inventory_date")
  private LocalDate roomTypeInventoryDate;

  @NotNull
  @Column(name = "total_inventory")
  private Integer totalInventory;

  @NotNull
  @Column(name = "total_reserved")
  private Integer totalReserved;


}
