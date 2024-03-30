package com.hotel.core.domain.repository;

import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.core.domain.ddd.AggregateRoot;
import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.PaginationResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BaseRepository<T extends AggregateRoot, F extends AuditFilters> {

    List<T> findAuditByFilters(F filters, int limit);

    PaginationResponse<T> search(Criteria criteria);

    PaginationResponse<T> searchBySelection(Criteria criteria);

    void save(T reservation);

    Optional<T> findById(UUID id);

}
