package com.reservation.application.usecase;

import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.reservation.domain.model.Reservation;
import com.reservation.domain.repository.ReservationRepository;
import com.reservation.domain.usecase.GetReservationsAuditUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetReservationsAuditUseCaseImpl implements GetReservationsAuditUseCase {

    private final ReservationRepository reservationRepository;

    @Override
    public List<Reservation> getReservationsAudit(final AuditFilters filters, final int limit) {
        return this.reservationRepository.findAuditByFilters(filters, limit);
    }
}
