package com.hotel.core.domain.repository;

import com.hotel.core.domain.ddd.AggregateRoot;
import com.hotel.core.infrastructure.database.audit.AuditFilters;

import java.util.List;

public interface BaseAuditRepository<T extends AggregateRoot, F extends AuditFilters> {

    List<T> findAuditByFilters(F filters, int limit);
}
