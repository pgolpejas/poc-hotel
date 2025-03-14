package com.reservation.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.Pagination;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.core.infrastructure.database.audit.JaversUtil;
import com.hotel.core.infrastructure.database.springfilter.CriteriaBuilderUtil;
import com.outbox.annotation.Outbox;
import com.reservation.domain.model.Reservation;
import com.reservation.domain.repository.ReservationRepository;
import com.reservation.infrastructure.repository.entity.ReservationEntity;
import com.reservation.infrastructure.repository.entity.ReservationEntity_;
import com.reservation.infrastructure.repository.jpa.ReservationJpaRepository;
import com.reservation.infrastructure.repository.mapper.ReservationRepositoryMapper;
import com.turkraft.springfilter.converter.FilterSpecification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReservationRepositoryAdapter implements ReservationRepository {

  private final ReservationJpaRepository reservationJpaRepository;

  private final ReservationRepositoryMapper reservationRepositoryMapper;

  private final CriteriaBuilderUtil criteriaBuilderUtil;

  private final JaversUtil javersUtil;

  @PersistenceContext
  private final EntityManager em;

  private static final String[] selectionFields = new String[]{
      ReservationEntity_.ID,
      ReservationEntity_.VERSION,
      ReservationEntity_.ROOM_TYPE_ID,
      ReservationEntity_.HOTEL_ID,
      ReservationEntity_.GUEST_ID,
      ReservationEntity_.START,
      ReservationEntity_.END
  };

  @Outbox
  @Override
  public void save(final Reservation reservation) {
    this.reservationJpaRepository.save(this.reservationRepositoryMapper.mapToEntity(reservation));
  }

  @Outbox
  @Override
  public void delete(final Reservation reservation) {
    this.reservationJpaRepository.deleteByPK(reservation.id());
  }

  @Transactional
  @Override
  public Optional<Reservation> findById(final UUID id) {
    return this.reservationJpaRepository.findAggregateById(id);
  }

  @Transactional
  @Override
  public boolean existsById(final UUID id) {
    return this.reservationJpaRepository.existsByPK(id);
  }

  @Override
  public PaginationResponse<Reservation> search(final Criteria criteria) {

    final Pageable pageable = this.criteriaBuilderUtil.pageableFromCriteria(criteria);

    final FilterSpecification<ReservationEntity> spec = this.criteriaBuilderUtil
        .springFilterToSpecification(criteria.filters());
    final Page<ReservationEntity> page =
        this.criteriaBuilderUtil.findPaginated(this.em, pageable, ReservationEntity.class, spec);

    return this.mapToPaginationResponse(criteria, page);
  }

  @Override
  public PaginationResponse<Reservation> searchBySelection(final Criteria criteria) {
    final FilterSpecification<ReservationEntity> spec = this.criteriaBuilderUtil
        .springFilterToSpecification(criteria.filters());

    final Function<Root<?>, Selection<?>[]> selectionId =
        root -> Stream.of(selectionFields).map(root::get).toArray(Selection[]::new);

    final Pageable pageable = this.criteriaBuilderUtil.pageableFromCriteria(criteria);

    final Page<ReservationEntity> page = this.criteriaBuilderUtil.findPaginatedSelection(this.em, pageable,
        ReservationEntity.class, spec, selectionId);

    return this.mapToPaginationResponse(criteria, page);
  }

  private PaginationResponse<Reservation> mapToPaginationResponse(Criteria criteria, Page<ReservationEntity> page) {
    final long totalItems = page.getTotalElements();
    final List<Reservation> reservations = page.stream()
        .map(this.reservationRepositoryMapper::mapToAggregate).toList();

    return PaginationResponse.<Reservation>builder()
        .pagination(Pagination.builder()
            .limit(criteria.limit())
            .page(criteria.page())
            .total(totalItems)
            .build())
        .data(reservations)
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Reservation> findAuditByFilters(AuditFilters filters, int limit) {
    final ReservationEntity entity = ReservationEntity.builder().id(filters.getId()).build();
    return this.javersUtil.findAuditByInstanceId(entity, filters, limit).stream()
        .filter(shadow -> shadow.getId() != null)
        .map(this.reservationRepositoryMapper::mapToAggregate).toList();
  }
}
