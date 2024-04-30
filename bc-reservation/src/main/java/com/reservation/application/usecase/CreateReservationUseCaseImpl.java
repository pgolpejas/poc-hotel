package com.reservation.application.usecase;

import com.reservation.domain.exception.ReservationConflictException;
import com.reservation.domain.model.Reservation;
import com.reservation.domain.model.RoomTypeInventory;
import com.reservation.domain.repository.ReservationRepository;
import com.reservation.domain.repository.RoomTypeInventoryRepository;
import com.reservation.domain.usecase.CreateReservationUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Validated
public class CreateReservationUseCaseImpl implements CreateReservationUseCase {

    private final ReservationRepository reservationRepository;
    private final RoomTypeInventoryRepository roomTypeInventoryRepository;


    @Transactional
    @Override
    public void createReservation(@Valid final Reservation reservation) {

        if (this.reservationRepository.existsById(reservation.id())) {
            throw new ReservationConflictException("Reservation with id %s already exists", reservation.id().toString());
        }

        final Reservation newReservation = Reservation.create(reservation);

        List<RoomTypeInventory> roomTypeInventories = this.roomTypeInventoryRepository
                .findByReservationDates(reservation.hotelId(), reservation.roomTypeId(), reservation.start(), reservation.end());
        if (!this.hasAvailability(roomTypeInventories, reservation.start(), reservation.end())) {
            throw new ReservationConflictException("Reservation [%s,%s,%s,%s] has not availability", reservation.hotelId(), reservation.roomTypeId(), reservation.start(), reservation.end());
        }
        
        roomTypeInventories.forEach(roomTypeInventory -> {
            roomTypeInventory.update(roomTypeInventory.totalInventory() - 1, roomTypeInventory.totalReserved() + 1);
            this.roomTypeInventoryRepository.save(roomTypeInventory);
        });

        this.reservationRepository.save(newReservation);
    }

    private boolean hasAvailability(List<RoomTypeInventory> roomTypeInventories, LocalDate start, LocalDate end) {
        final long days = Duration.between(start.atStartOfDay(), end.atStartOfDay()).toDays() + 1;

        if (days != roomTypeInventories.size()) {
            return false;
        }
        return roomTypeInventories.stream().allMatch(roomTypeInventory -> roomTypeInventory.totalInventory() > 0);
    }
}
