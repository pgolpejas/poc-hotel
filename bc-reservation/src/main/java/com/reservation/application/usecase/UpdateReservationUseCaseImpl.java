package com.reservation.application.usecase;

import com.reservation.domain.exception.ReservationNotFoundException;
import com.reservation.domain.model.Reservation;
import com.reservation.domain.repository.ReservationRepository;
import com.reservation.domain.repository.RoomTypeInventoryRepository;
import com.reservation.domain.usecase.UpdateReservationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UpdateReservationUseCaseImpl implements UpdateReservationUseCase {

    private final ReservationRepository reservationRepository;
    private final RoomTypeInventoryRepository roomTypeInventoryRepository;

    @Transactional
    @Override
    public void updateReservation(final Reservation reservation) {

        Optional<Reservation> optionalReservation = reservationRepository.findById(reservation.id());

        if (optionalReservation.isEmpty()) {
            throw new ReservationNotFoundException("Reservation with id %s not found", reservation.id().toString());
        } else {
            Reservation aggregate = optionalReservation.get();
            
            if (!reservation.start().isEqual(aggregate.start()) || !reservation.end().isEqual(aggregate.end())) {
                this.roomTypeInventoryRepository
                        .findByReservationDates(aggregate.hotelId(), aggregate.roomTypeId(), aggregate.start(), aggregate.end())
                        .forEach(roomTypeInventory -> {
                            roomTypeInventory.update(roomTypeInventory.totalInventory() + 1, roomTypeInventory.totalReserved() - 1);
                            this.roomTypeInventoryRepository.save(roomTypeInventory);
                        });
                
                this.roomTypeInventoryRepository
                        .findByReservationDates(reservation.hotelId(), reservation.roomTypeId(), reservation.start(), reservation.end())
                        .forEach(roomTypeInventory -> {
                            roomTypeInventory.update(roomTypeInventory.totalInventory() - 1, roomTypeInventory.totalReserved() + 1);
                            this.roomTypeInventoryRepository.save(roomTypeInventory);
                        });
            }
            
            aggregate.update(reservation);
            this.reservationRepository.save(aggregate);
        }
    }
}
