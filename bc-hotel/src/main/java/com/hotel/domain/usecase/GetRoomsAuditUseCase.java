package com.hotel.domain.usecase;

import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.domain.model.Room;

import java.util.List;

public interface GetRoomsAuditUseCase {

    List<Room> getRoomsAudit(AuditFilters filters, int limit);
}
