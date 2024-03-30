package com.hotel.core.domain.repository;

import com.hotel.core.domain.ddd.AggregateRoot;
import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.PaginationResponse;

import java.util.Optional;
import java.util.UUID;

public interface BaseRepository<T extends AggregateRoot> {

    PaginationResponse<T> search(Criteria criteria);

    void save(T reservation);

    void delete(T reservation);

    Optional<T> findById(UUID id);

}
