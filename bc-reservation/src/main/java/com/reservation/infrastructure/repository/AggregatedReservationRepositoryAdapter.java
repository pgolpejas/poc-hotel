package com.reservation.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.hotel.core.domain.dto.Pagination;
import com.hotel.core.domain.dto.PaginationResponse;
import com.reservation.domain.dto.AggregatedReservationCriteria;
import com.reservation.domain.exception.AggregatedReservationNotFoundException;
import com.reservation.domain.model.AggregatedReservation;
import com.reservation.domain.repository.AggregatedReservationRepository;
import com.reservation.infrastructure.repository.entity.AggregatedReservationEntity;
import com.reservation.infrastructure.repository.jpa.AggregatedReservationJpaRepository;
import com.reservation.infrastructure.repository.mapper.AggregatedReservationRepositoryMapper;
import com.reservation.infrastructure.repository.mongo.entity.AggregatedReservationDocument;
import com.reservation.infrastructure.repository.mongo.repository.AggregatedReservationMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class AggregatedReservationRepositoryAdapter implements AggregatedReservationRepository {

  private final AggregatedReservationJpaRepository aggregatedReservationJpaRepository;

  private final AggregatedReservationMongoRepository aggregatedReservationMongoRepository;

  private final AggregatedReservationRepositoryMapper aggregatedReservationRepositoryMapper;

  @Transactional
  @Override
  public boolean existsById(final UUID id) {
    return this.aggregatedReservationJpaRepository.existsByPK(id);
  }

  @Transactional
  @Override
  public Optional<AggregatedReservation> findById(final UUID id) {
    return this.aggregatedReservationJpaRepository.findById(id)
        .map(this.aggregatedReservationRepositoryMapper::mapToAggregate);
  }

  @Transactional
  @Override
  public void save(final AggregatedReservation aggregate) {
    final AggregatedReservationEntity entity = this.aggregatedReservationRepositoryMapper.mapToEntity(aggregate);
    this.aggregatedReservationJpaRepository.save(entity);
  }

  @Transactional
  @Override
  public void update(final AggregatedReservation aggregate) {
    final AggregatedReservationEntity entity = this.aggregatedReservationJpaRepository.findById(aggregate.getId())
        .orElseThrow(() -> new AggregatedReservationNotFoundException("Reservation with id %s not found", aggregate.getId()));
    entity.setHotelId(aggregate.getHotelId());
    entity.setStart(aggregate.getStart());
    entity.setEnd(aggregate.getEnd());
    entity.setReservation(aggregate.getReservation());
    this.aggregatedReservationJpaRepository.save(entity);
  }

  @Transactional
  @Override
  public void deleteById(UUID id) {
    this.aggregatedReservationJpaRepository.deleteById(id);
  }

  @Override
  public PaginationResponse<AggregatedReservation> searchBySelection(final AggregatedReservationCriteria criteria) {
    final Page<AggregatedReservationEntity> page = this.aggregatedReservationJpaRepository.search(criteria);

    final List<AggregatedReservation> result = page.stream()
        .map(this.aggregatedReservationRepositoryMapper::mapToAggregate)
        .toList();

    return PaginationResponse.<AggregatedReservation>builder()
        .data(result)
        .pagination(Pagination.builder()
            .page(criteria.page())
            .limit(criteria.limit())
            .total(page.getTotalElements())
            .build())
        .build();
  }

  @Override
  public PaginationResponse<AggregatedReservation> searchByMongo(AggregatedReservationCriteria criteria) {
    final Page<AggregatedReservationDocument> page = this.aggregatedReservationMongoRepository.search(criteria);

    final List<AggregatedReservation> result = page.stream()
        .map(this.aggregatedReservationRepositoryMapper::mapToAggregate)
        .toList();

    return PaginationResponse.<AggregatedReservation>builder()
        .data(result)
        .pagination(Pagination.builder()
            .page(criteria.page())
            .limit(criteria.limit())
            .total(page.getTotalElements())
            .build())
        .build();
  }

  @Override
  public void saveMongo(AggregatedReservation aggregatedReservation) {
    final AggregatedReservationDocument document = this.aggregatedReservationRepositoryMapper.mapToDocument(aggregatedReservation);
    this.aggregatedReservationMongoRepository.save(document);
  }
}
