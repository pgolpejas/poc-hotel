package com.hotel.infrastructure.repository;

import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.Pagination;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.core.infrastructure.database.audit.JaversUtil;
import com.hotel.core.infrastructure.database.springfilter.CriteriaBuilderUtil;
import com.hotel.domain.model.Room;
import com.hotel.domain.repository.RoomRepository;
import com.hotel.infrastructure.entity.RoomEntity;
import com.hotel.infrastructure.entity.RoomEntity_;
import com.hotel.infrastructure.repository.jpa.RoomJpaRepository;
import com.hotel.infrastructure.repository.mapper.RoomRepositoryMapper;
import com.outbox.annotation.Outbox;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

@Component
@Slf4j
@RequiredArgsConstructor
public class RoomRepositoryAdapter implements RoomRepository {

    private final RoomJpaRepository roomJpaRepository;

    private final RoomRepositoryMapper roomRepositoryMapper;

    private final CriteriaBuilderUtil criteriaBuilderUtil;

    private final JaversUtil javersUtil;

    @PersistenceContext
    private final EntityManager em;

    private static final String[] selectionFields = new String[]{
            RoomEntity_.ID,
            RoomEntity_.VERSION,
            RoomEntity_.ROOM_TYPE_ID,
            RoomEntity_.HOTEL_ID,
            RoomEntity_.NAME,
            RoomEntity_.FLOOR,
            RoomEntity_.ROOM_NUMBER,
            RoomEntity_.AVAILABLE
    };

    @Outbox
    @Override
    public void save(final Room room) {
        this.roomJpaRepository.save(this.roomRepositoryMapper.mapToEntity(room));
    }

    @Outbox
    @Override
    public void delete(final Room room) {
        this.roomJpaRepository.deleteByPK(room.id());
    }

    @Transactional
    @Override
    public Optional<Room> findById(final UUID id) {
        return this.roomJpaRepository.findAggregateById(id);
    }

    @Transactional
    @Override
    public boolean existsById(final UUID id) {
        return this.roomJpaRepository.existsByPK(id);
    }

    @Transactional
    @Override
    public boolean existsByUK(final UUID hotelId, final Integer roomTypeId, final int floor, final String roomNumber) {
        return this.roomJpaRepository.existsByUK(hotelId, roomTypeId, floor, roomNumber);
    }

    @Transactional
    @Override
    public Optional<Room> findByUK(UUID hotelId, Integer roomTypeId, final int floor, final String roomNumber) {
        return this.roomJpaRepository.findByUK(hotelId, roomTypeId, floor, roomNumber);
    }

    @Override
    public PaginationResponse<Room> search(final Criteria criteria) {

        final Pageable pageable = criteriaBuilderUtil.pageableFromCriteria(criteria);

        final FilterSpecification<RoomEntity> spec = this.criteriaBuilderUtil
                .springFilterToSpecification(criteria.filters());
        final Page<RoomEntity> page =
                this.criteriaBuilderUtil.findPaginated(this.em, pageable, RoomEntity.class, spec);

        return mapToPaginationResponse(criteria, page);
    }

    @Override
    public PaginationResponse<Room> searchBySelection(final Criteria criteria) {
        final FilterSpecification<RoomEntity> spec = criteriaBuilderUtil
                .springFilterToSpecification(criteria.filters());

        final Function<Root<?>, Selection<?>[]> selectionId =
                root -> Stream.of(selectionFields).map(root::get).toArray(Selection[]::new);

        final Pageable pageable = criteriaBuilderUtil.pageableFromCriteria(criteria);

        final Page<RoomEntity> page = criteriaBuilderUtil.findPaginatedSelection(em, pageable,
                RoomEntity.class, spec, selectionId);

        return mapToPaginationResponse(criteria, page);
    }

    private PaginationResponse<Room> mapToPaginationResponse(Criteria criteria, Page<RoomEntity> page) {
        final long totalItems = page.getTotalElements();
        final List<Room> roomTypeInventories = page.stream()
                .map(this.roomRepositoryMapper::mapToAggregate).toList();

        return PaginationResponse.<Room>builder()
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
    public List<Room> findAuditByFilters(AuditFilters filters, int limit) {
        final RoomEntity entity = RoomEntity.builder().id(filters.getId()).build();
        return javersUtil.findAuditByInstanceId(entity, filters, limit).stream()
                .filter(shadow -> shadow.getId() != null)
                .map(this.roomRepositoryMapper::mapToAggregate).toList();
    }
}
