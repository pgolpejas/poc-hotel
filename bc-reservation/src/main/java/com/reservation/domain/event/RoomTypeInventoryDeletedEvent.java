package com.reservation.domain.event;

import java.io.Serial;
import java.util.UUID;

import com.hotel.core.domain.ddd.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RoomTypeInventoryDeletedEvent implements DomainEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;

    public String getAggregateId() {
        return this.id.toString();
    }

    @Override
    public String getActionType() {
        return "RoomTypeInventoryDeleted";
    }
}
