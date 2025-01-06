package com.hotel.domain.usecase;

import java.util.List;

import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.domain.model.Hotel;

public interface GetHotelsAuditQuery {

  List<Hotel> getHotelsAudit(AuditFilters filters, int limit);
}
