package com.hotel.infrastructure.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "room")
@SuppressWarnings("squid:S1170")
public class RoomEntity implements Serializable {

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

    @Size(max = 100)
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "floor")
    private int floor;

    @NotBlank
    @Column(name = "room_number")
    private String roomNumber;

    @Column(name = "available")
    private boolean available;

}
