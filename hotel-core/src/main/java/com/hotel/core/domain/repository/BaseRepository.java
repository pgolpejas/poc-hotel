package com.hotel.core.domain.repository;

import com.hotel.core.domain.ddd.AggregateRoot;
import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.PaginationResponse;

import java.util.Optional;

@SuppressWarnings("java:S119")
public interface BaseRepository<T extends AggregateRoot, ID> {

    boolean existsById(ID id);

    PaginationResponse<T> search(Criteria criteria);

    void save(T entity);

    void delete(T entity);

    Optional<T> findById(ID id);

}
