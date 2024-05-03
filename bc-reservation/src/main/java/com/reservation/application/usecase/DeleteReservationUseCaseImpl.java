package com.reservation.application.usecase;

import com.reservation.domain.exception.ReservationNotFoundException;
import com.reservation.domain.model.Reservation;
import com.reservation.domain.repository.ReservationRepository;
import com.reservation.domain.repository.RoomTypeInventoryRepository;
import com.reservation.domain.usecase.DeleteReservationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteReservationUseCaseImpl implements DeleteReservationUseCase {

    private final ReservationRepository reservationRepository;
    private final RoomTypeInventoryRepository roomTypeInventoryRepository;

    @Transactional
    @Override
    public void deleteReservation(final UUID id) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(id);

        if (optionalReservation.isEmpty()) {
            throw new ReservationNotFoundException("Reservation with id %s not found", id.toString());
        } else {
            Reservation reservation = optionalReservation.get();
            
            this.roomTypeInventoryRepository
                    .findByReservationDates(reservation.hotelId(), reservation.roomTypeId(), reservation.start(), reservation.end())
                    .forEach(roomTypeInventory -> {
                        roomTypeInventory.update(roomTypeInventory.totalInventory() + 1, roomTypeInventory.totalReserved() - 1);
                        this.roomTypeInventoryRepository.save(roomTypeInventory);
                    });

            reservation.delete();
            this.reservationRepository.delete(reservation);
        }
    }
}
