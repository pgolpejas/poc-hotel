package com.reservation.domain.usecase;

import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.reservation.domain.model.Reservation;

import java.util.List;

public interface GetReservationsAuditUseCase {

    List<Reservation> getReservationsAudit(AuditFilters filters, int limit);
}
