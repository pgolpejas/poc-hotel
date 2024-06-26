package com.reservation.infrastructure.producer.mapper;

import java.util.UUID;

import com.reservation.domain.avro.v1.ReservationCreated;
import com.reservation.domain.avro.v1.ReservationDeleted;
import com.reservation.domain.avro.v1.ReservationSnapshot;
import com.reservation.domain.avro.v1.ReservationUpdated;
import com.reservation.domain.event.ReservationDomainEvent.ReservationCreatedEvent;
import com.reservation.domain.event.ReservationDomainEvent.ReservationDeletedEvent;
import com.reservation.domain.event.ReservationDomainEvent.ReservationUpdatedEvent;
import com.reservation.domain.model.GuestId;
import com.reservation.domain.model.HotelId;
import com.reservation.domain.model.Reservation;
import com.reservation.domain.model.ReservationId;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
@SuppressWarnings("java:S6204")
public interface ReservationEventMapper {

  ReservationSnapshot mapReservation(Reservation aggregate);

  ReservationCreated mapReservationCreated(ReservationCreatedEvent event);

  ReservationDeleted mapReservationDeleted(ReservationDeletedEvent event);

  ReservationUpdated mapReservationUpdated(ReservationUpdatedEvent event);

  default UUID mapId(final ReservationId id) {
    return id.value();
  }

  default UUID mapHotelId(final HotelId hotelId) {
    return hotelId.value();
  }

  default UUID mapGuestId(final GuestId guestId) {
    return guestId.value();
  }
}
