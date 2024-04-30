package com.reservation.infrastructure.repository;

import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.Pagination;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.core.infrastructure.database.audit.JaversUtil;
import com.hotel.core.infrastructure.database.springfilter.CriteriaBuilderUtil;
import com.outbox.annotation.Outbox;
import com.reservation.domain.model.RoomTypeInventory;
import com.reservation.domain.repository.RoomTypeInventoryRepository;
import com.reservation.infrastructure.entity.RoomTypeInventoryEntity;
import com.reservation.infrastructure.entity.RoomTypeInventoryEntity_;
import com.reservation.infrastructure.repository.jpa.RoomTypeInventoryJpaRepository;
import com.reservation.infrastructure.repository.mapper.RoomTypeInventoryRepositoryMapper;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

@Component
@Slf4j
@RequiredArgsConstructor
public class RoomTypeInventoryRepositoryAdapter implements RoomTypeInventoryRepository {

    private final RoomTypeInventoryJpaRepository roomTypeInventoryJpaRepository;

    private final RoomTypeInventoryRepositoryMapper roomTypeInventoryRepositoryMapper;

    private final CriteriaBuilderUtil criteriaBuilderUtil;

    private final JaversUtil javersUtil;

    @PersistenceContext
    private final EntityManager em;

    private static final String[] selectionFields = new String[]{
            RoomTypeInventoryEntity_.ID,
            RoomTypeInventoryEntity_.VERSION,
            RoomTypeInventoryEntity_.ROOM_TYPE_ID,
            RoomTypeInventoryEntity_.HOTEL_ID,
            RoomTypeInventoryEntity_.ROOM_TYPE_INVENTORY_DATE,
            RoomTypeInventoryEntity_.TOTAL_INVENTORY,
            RoomTypeInventoryEntity_.TOTAL_RESERVED
    };

    @Outbox
    @Override
    public void save(final RoomTypeInventory roomTypeInventory) {
        this.roomTypeInventoryJpaRepository.save(this.roomTypeInventoryRepositoryMapper.mapToEntity(roomTypeInventory));
    }

    @Outbox
    @Override
    public void delete(final RoomTypeInventory roomTypeInventory) {
        this.roomTypeInventoryJpaRepository.deleteByPK(roomTypeInventory.id());
    }

    @Transactional
    @Override
    public Optional<RoomTypeInventory> findById(final UUID id) {
        return this.roomTypeInventoryJpaRepository.findAggregateById(id);
    }

    @Transactional
    @Override
    public boolean existsById(final UUID id) {
        return this.roomTypeInventoryJpaRepository.existsByPK(id);
    }

    @Transactional
    @Override
    public boolean existsByUK(final UUID hotelId, final Integer roomTypeId, final LocalDate roomTypeInventoryDate) {
        return this.roomTypeInventoryJpaRepository.existsByUK(hotelId, roomTypeId, roomTypeInventoryDate);
    }

    @Transactional
    @Override
    public Optional<RoomTypeInventory> findByUK(UUID hotelId, Integer roomTypeId, LocalDate roomTypeInventoryDate) {
        return this.roomTypeInventoryJpaRepository.findByUK(hotelId, roomTypeId, roomTypeInventoryDate);
    }

    @Override
    public List<RoomTypeInventory> findByReservationDates(UUID hotelId, Integer roomTypeId, LocalDate start, LocalDate end) {
        return this.roomTypeInventoryJpaRepository.findByReservationDates(hotelId, roomTypeId, start, end);
    }

    @Override
    public PaginationResponse<RoomTypeInventory> search(final Criteria criteria) {

        final Pageable pageable = criteriaBuilderUtil.pageableFromCriteria(criteria);

        final FilterSpecification<RoomTypeInventoryEntity> spec = this.criteriaBuilderUtil
                .springFilterToSpecification(criteria.filters());
        final Page<RoomTypeInventoryEntity> page =
                this.criteriaBuilderUtil.findPaginated(this.em, pageable, RoomTypeInventoryEntity.class, spec);

        return mapToPaginationResponse(criteria, page);
    }

    @Override
    public PaginationResponse<RoomTypeInventory> searchBySelection(final Criteria criteria) {
        final FilterSpecification<RoomTypeInventoryEntity> spec = criteriaBuilderUtil
                .springFilterToSpecification(criteria.filters());

        final Function<Root<?>, Selection<?>[]> selectionId =
                root -> Stream.of(selectionFields).map(root::get).toArray(Selection[]::new);

        final Pageable pageable = criteriaBuilderUtil.pageableFromCriteria(criteria);

        final Page<RoomTypeInventoryEntity> page = criteriaBuilderUtil.findPaginatedSelection(em, pageable,
                RoomTypeInventoryEntity.class, spec, selectionId);

        return mapToPaginationResponse(criteria, page);
    }

    private PaginationResponse<RoomTypeInventory> mapToPaginationResponse(Criteria criteria, Page<RoomTypeInventoryEntity> page) {
        final long totalItems = page.getTotalElements();
        final List<RoomTypeInventory> roomTypeInventories = page.stream()
                .map(this.roomTypeInventoryRepositoryMapper::mapToAggregate).toList();

        return PaginationResponse.<RoomTypeInventory>builder()
                .pagination(Pagination.builder()
                        .limit(criteria.limit())
                        .page(criteria.page())
                        .total(totalItems)
                        .build())
                .data(roomTypeInventories)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomTypeInventory> findAuditByFilters(AuditFilters filters, int limit) {
        final RoomTypeInventoryEntity entity = RoomTypeInventoryEntity.builder().id(filters.getId()).build();
        return javersUtil.findAuditByInstanceId(entity, filters, limit).stream()
                .filter(shadow -> shadow.getId() != null)
                .map(this.roomTypeInventoryRepositoryMapper::mapToAggregate).toList();
    }
}
