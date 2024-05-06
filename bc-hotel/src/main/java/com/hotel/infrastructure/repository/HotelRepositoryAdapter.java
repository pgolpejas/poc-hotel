package com.hotel.infrastructure.repository;

import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.Pagination;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.core.infrastructure.database.audit.JaversUtil;
import com.hotel.core.infrastructure.database.springfilter.CriteriaBuilderUtil;
import com.hotel.domain.model.Hotel;
import com.hotel.domain.repository.HotelRepository;
import com.hotel.infrastructure.entity.HotelEntity;
import com.hotel.infrastructure.entity.HotelEntity_;
import com.hotel.infrastructure.repository.jpa.HotelJpaRepository;
import com.hotel.infrastructure.repository.mapper.HotelRepositoryMapper;
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
public class HotelRepositoryAdapter implements HotelRepository {

    private final HotelJpaRepository hotelJpaRepository;

    private final HotelRepositoryMapper hotelRepositoryMapper;

    private final CriteriaBuilderUtil criteriaBuilderUtil;

    private final JaversUtil javersUtil;

    @PersistenceContext
    private final EntityManager em;

    private static final String[] selectionFields = new String[]{
            HotelEntity_.ID,
            HotelEntity_.VERSION,
            HotelEntity_.NAME,
            HotelEntity_.ADDRESS,
            HotelEntity_.CITY,
            HotelEntity_.COUNTRY,
            HotelEntity_.STATE,
            HotelEntity_.POSTAL_CODE,
    };

    @Outbox
    @Override
    public void save(final Hotel hotel) {
        this.hotelJpaRepository.save(this.hotelRepositoryMapper.mapToEntity(hotel));
    }

    @Outbox
    @Override
    public void delete(final Hotel hotel) {
        this.hotelJpaRepository.deleteByPK(hotel.id());
    }

    @Transactional
    @Override
    public Optional<Hotel> findById(final UUID id) {
        return this.hotelJpaRepository.findAggregateById(id);
    }

    @Transactional
    @Override
    public boolean existsById(final UUID id) {
        return this.hotelJpaRepository.existsByPK(id);
    }

    @Override
    public PaginationResponse<Hotel> search(final Criteria criteria) {

        final Pageable pageable = criteriaBuilderUtil.pageableFromCriteria(criteria);

        final FilterSpecification<HotelEntity> spec = this.criteriaBuilderUtil
                .springFilterToSpecification(criteria.filters());
        final Page<HotelEntity> page =
                this.criteriaBuilderUtil.findPaginated(this.em, pageable, HotelEntity.class, spec);

        return mapToPaginationResponse(criteria, page);
    }

    @Override
    public PaginationResponse<Hotel> searchBySelection(final Criteria criteria) {
        final FilterSpecification<HotelEntity> spec = criteriaBuilderUtil
                .springFilterToSpecification(criteria.filters());

        final Function<Root<?>, Selection<?>[]> selectionId =
                root -> Stream.of(selectionFields).map(root::get).toArray(Selection[]::new);

        final Pageable pageable = criteriaBuilderUtil.pageableFromCriteria(criteria);

        final Page<HotelEntity> page = criteriaBuilderUtil.findPaginatedSelection(em, pageable,
                HotelEntity.class, spec, selectionId);

        return mapToPaginationResponse(criteria, page);
    }

    @Override
    public boolean existsByUK(final String name) {
        return this.hotelJpaRepository.existsByUK(name);
    }

    private PaginationResponse<Hotel> mapToPaginationResponse(Criteria criteria, Page<HotelEntity> page) {
        final long totalItems = page.getTotalElements();
        final List<Hotel> hotels = page.stream()
                .map(this.hotelRepositoryMapper::mapToAggregate).toList();

        return PaginationResponse.<Hotel>builder()
                .pagination(Pagination.builder()
                        .limit(criteria.limit())
                        .page(criteria.page())
                        .total(totalItems)
                        .build())
                .data(hotels)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hotel> findAuditByFilters(AuditFilters filters, int limit) {
        final HotelEntity entity = HotelEntity.builder().id(filters.getId()).build();
        return javersUtil.findAuditByInstanceId(entity, filters, limit).stream()
                .filter(shadow -> shadow.getId() != null)
                .map(this.hotelRepositoryMapper::mapToAggregate).toList();
    }
}
