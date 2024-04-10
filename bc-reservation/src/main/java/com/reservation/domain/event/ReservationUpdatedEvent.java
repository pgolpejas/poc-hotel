package com.reservation.domain.event;

import com.hotel.core.domain.ddd.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReservationUpdatedEvent implements DomainEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;

    private int version;

    private UUID hotelId;

    Integer roomTypeId;

    private UUID guestId;

    private LocalDate start;

    private LocalDate end;

    private String status;

    public String getAggregateId() {
        return this.id.toString();
    }

    @Override
    public String getActionType() {
        return "ReservationUpdated";
    }
}
