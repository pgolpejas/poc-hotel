package com.hotel.domain.usecase;

import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.domain.model.Hotel;

import java.util.List;

public interface GetHotelsAuditUseCase {

    List<Hotel> getHotelsAudit(AuditFilters filters, int limit);
}
