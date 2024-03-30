package com.reservation.domain.model;

import com.reservation.domain.core.AggregateRoot;
import com.reservation.domain.core.DomainError;
import com.reservation.domain.utils.ValidationUtils;
import lombok.Builder;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Reservation extends AggregateRoot implements Serializable {

    @Serial
    private static final long serialVersionUID = 7619183896070437504L;

    ReservationId id;

    int version;

    Integer roomTypeId;

    HotelId hotelId;

    GuestId guestId;

    LocalDate start;

    LocalDate end;

    String status;


    private Reservation() {
    }

    @Builder
    public Reservation(final UUID id,
                       final int version,
                       final Integer roomTypeId,
                       final UUID hotelId,
                       final UUID guestId,
                       final LocalDate start,
                       final LocalDate end,
                       final String status

    ) {

        // Common validations for all actions on the aggregate
        ValidationUtils.notNull(start, "start can not be null");
        ValidationUtils.notNull(end, "end can not be null");

        this.id = new ReservationId(id);
        this.version = version;
        this.roomTypeId = roomTypeId;
        this.hotelId = new HotelId(hotelId);
        this.guestId = new GuestId(guestId);
        this.start = start;
        this.end = end;
        this.status = status;
    }

    public static Reservation create(final Reservation reservation) {

        // Common domain validations to this action (include db validations)
        validateDatesToCreate(reservation);
        
        Reservation.builder()
                .id(reservation.id())
                .hotelId(reservation.hotelId())
                .roomTypeId(reservation.roomTypeId())
                .guestId(reservation.guestId())
                .start(reservation.start())
                .end(reservation.end())
                .status(reservation.status())
                .build();

        return reservation;
    }


    private static void validateDatesToCreate(final Reservation reservation) {
        if (reservation.start().isAfter(reservation.end())) {
            throw new DomainError(
                    String.format("Start date: %s cannot be later than end date: %s", reservation.start(), reservation.end()));
        }
    }

    public UUID id() {
        return this.id.value();
    }

    @Override
    public String getAggregateId() {
        return Objects.isNull(this.id) ? "" : this.id.value().toString();
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
