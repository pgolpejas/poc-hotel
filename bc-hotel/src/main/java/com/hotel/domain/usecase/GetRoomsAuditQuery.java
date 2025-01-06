package com.hotel.domain.usecase;

import java.util.List;

import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.domain.model.Room;

public interface GetRoomsAuditQuery {

  List<Room> getRoomsAudit(AuditFilters filters, int limit);
}
