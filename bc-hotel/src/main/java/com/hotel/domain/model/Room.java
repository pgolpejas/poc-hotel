package com.hotel.domain.model;

import com.hotel.core.domain.ddd.AggregateRoot;
import com.hotel.core.domain.ddd.DomainError;
import com.hotel.core.domain.utils.ValidationUtils;
import com.hotel.domain.event.RoomDomainEvent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings({"java:S107"})
public class Room extends AggregateRoot implements Serializable {

    @Serial
    private static final long serialVersionUID = 7611233896070437504L;

    RoomId id;

    int version;

    @NotNull
    Integer roomTypeId;

    HotelId hotelId;

    @Size(max = 100)
    String name;

    int floor;

    @NotBlank @Size(max = 5)
    String roomNumber;

    boolean available;

    private Room() {
    }

    @Builder
    public Room(@NotNull final UUID id,
                final int version,
                @NotNull final Integer roomTypeId,
                @NotNull final UUID hotelId,
                @Size(max = 100) final String name,
                @NotNull final int floor,
                @NotBlank @Size(max = 5) final String roomNumber,
                final boolean available
    ) {

        this.id = new RoomId(id);
        this.version = version;
        this.roomTypeId = roomTypeId;
        this.hotelId = new HotelId(hotelId);
        this.name = name;
        this.floor = floor;
        this.roomNumber = roomNumber;
        this.available = available;

        commonValidations(this);

    }

    public static Room create(final Room room) {

        // Common domain validations to this action (include db validations)
        commonValidations(room);

        final Room newAggregate = Room.builder()
                .id(room.id())
                .hotelId(room.hotelId())
                .roomTypeId(room.roomTypeId())
                .floor(room.floor)
                .roomNumber(room.roomNumber())
                .available(room.available())
                .build();

        newAggregate.registerEvent(
                RoomDomainEvent.RoomCreatedEvent.builder()
                        .id(newAggregate.id())
                        .roomTypeId(newAggregate.getRoomTypeId())
                        .hotelId(newAggregate.hotelId())
                        .name(newAggregate.name())
                        .floor(newAggregate.floor())
                        .roomNumber(newAggregate.roomNumber())
                        .available(newAggregate.available())
                        .build());

        return newAggregate;
    }

    private static void commonValidations(final Room room) {
        ValidationUtils.notNull(room.roomNumber(), "roomNumber can not be null");
        ValidationUtils.maxSize(room.roomNumber(), 5, "roomNumber can not be greater than 5 characters");
        ValidationUtils.maxSize(room.name, 100, "name can not be greater than 100 characters");

        if (room.floor() < -10) {
            throw new DomainError("Total inventory can not be negative");
        }
    }

    public void delete() {
        this.registerEvent(RoomDomainEvent.RoomDeletedEvent.builder()
                .id(this.id())
                .build());
    }

    public void update(final Room room) {
        this.name = room.name();
        this.floor = room.floor();
        this.roomNumber = room.roomNumber();
        this.available = room.available();

        commonValidations(this);

        this.registerEvent(
                RoomDomainEvent.RoomUpdatedEvent.builder()
                        .id(this.id())
                        .roomTypeId(this.getRoomTypeId())
                        .hotelId(this.hotelId())
                        .name(this.name())
                        .floor(this.floor())
                        .roomNumber(this.roomNumber())
                        .available(this.available())
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
