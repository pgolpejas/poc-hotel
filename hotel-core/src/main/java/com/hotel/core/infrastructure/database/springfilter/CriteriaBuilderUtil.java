package com.hotel.core.infrastructure.database.springfilter;

import com.hotel.core.domain.dto.Criteria;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
@Slf4j
@RequiredArgsConstructor
public class CriteriaBuilderUtil {

    private final FilterSpecificationConverter filterSpecificationConverter;

    public <T> FilterSpecification<T> springFilterToSpecification(final String filter) {
        return this.filterSpecificationConverter.convert(filter);
    }

    public Pageable pageableFromCriteria(final Criteria criteria) {
        Sort sort = Sort.unsorted();

        if (StringUtils.hasText(criteria.sortBy())) {
            Sort.Direction sortDirection = Sort.Direction.ASC;
            if (StringUtils.hasText(criteria.sortDirection())) {
                sortDirection = Sort.Direction.fromString(criteria.sortDirection());
            }
            sort = Sort.by(sortDirection, criteria.sortBy());
        }

        return PageRequest.of(criteria.page(), criteria.limit(), sort);
    }

    public <T> Page<T> findPaginatedWithDistinct(final EntityManager em, final Pageable page, final Class<T> entityClass,
                                                 final Specification<T> specification) {
        return this.findPaginatedSelectionByPk(em, page, entityClass, entityClass, specification, null, true);
    }

    public <T> Page<T> findPaginated(final EntityManager em, final Pageable page, final Class<T> entityClass,
                                     final Specification<T> specification) {
        return this.findPaginatedSelectionByPk(em, page, entityClass, entityClass, specification, null, false);
    }

    public <T, U> Page<U> findPaginatedSelectionByPk(final EntityManager em,
                                                     final Pageable page,
                                                     final Class<T> entityClass,
                                                     final Class<U> entitySelectionClass,
                                                     final Specification<T> specification,
                                                     final Function<Root<?>, Selection<?>[]> selectionId,
                                                     final boolean withDistinctValues) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<U> cqSelection = cb.createQuery(entitySelectionClass);

        final Root<T> root = cqSelection.from(entityClass);
        final List<Predicate> predicatesSelection = this.specToPredicatesList(specification, root, cqSelection, cb);

        final CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        final Root<T> rootCount = countQuery.from(entityClass);
        if (withDistinctValues) {
            // Doesn't work in Oracle due to hibernation bug
            countQuery.select(cb.countDistinct(rootCount));
        } else {
            countQuery.select(cb.count(rootCount));
        }

        final List<Predicate> predicatesCount = this.specToPredicatesList(specification, rootCount, countQuery, cb);

        countQuery.where(predicatesCount.toArray(new Predicate[]{}));

        final TypedQuery<Long> typedCountQuery = em.createQuery(countQuery);
        typedCountQuery.setFlushMode(FlushModeType.COMMIT);
        final Long count = typedCountQuery.getSingleResult();

        cqSelection.where(predicatesSelection.toArray(new Predicate[]{}));
        cqSelection.orderBy(QueryUtils.toOrders(page.getSort(), root, cb));
        if (null != selectionId) {
            cqSelection.multiselect(selectionId.apply(root));
        }
        if (withDistinctValues) {
            cqSelection.distinct(true);
        }

        final TypedQuery<U> typedQuery = em.createQuery(cqSelection);
        typedQuery.setFlushMode(FlushModeType.COMMIT);
        if (page.isPaged()) {
            typedQuery.setFirstResult(page.getPageNumber() * page.getPageSize());
            typedQuery.setMaxResults(page.getPageSize());
        }

        final List<U> result = typedQuery.getResultList();
        return new PageImpl<>(result, page, count);
    }


    private <T> List<Predicate> specToPredicatesList(final Specification<T> specification, final Root<T> root, final CriteriaQuery<?> cq,
                                                     final CriteriaBuilder cb) {
        final List<Predicate> predicates = new ArrayList<>();
        if (null != specification) {
            final Predicate predicate = specification.toPredicate(root, cq, cb);
            predicates.add(predicate);
        }
        return predicates;
    }

    public <T> Page<T> findPaginatedSelection(final EntityManager em,
                                              final Pageable page,
                                              final Class<T> entityClass,
                                              final Specification<T> specification,
                                              final Function<Root<?>, Selection<?>[]> selectionFields) {

        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<T> cqSelection = cb.createQuery(entityClass);

        final Root<T> root = cqSelection.from(entityClass);
        final List<Predicate> predicatesSelection = this.specToPredicatesList(specification, root, cqSelection, cb);
        final CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);

        final Root<T> rootCount = countQuery.from(entityClass);
        countQuery.select(cb.count(rootCount));
        final List<Predicate> predicatesCount = this.specToPredicatesList(specification, rootCount, countQuery, cb);

        countQuery.where(predicatesCount.toArray(new Predicate[0]));
        final TypedQuery<Long> typedCountQuery = em.createQuery(countQuery);
        typedCountQuery.setFlushMode(FlushModeType.COMMIT);
        final Long count = typedCountQuery.getSingleResult();
        cqSelection.where(predicatesSelection.toArray(new Predicate[0]));
        cqSelection.orderBy(QueryUtils.toOrders(page.getSort(), root, cb));
        cqSelection.multiselect(selectionFields.apply(root));

        final TypedQuery<T> typedQuery = em.createQuery(cqSelection);
        typedQuery.setFlushMode(FlushModeType.COMMIT);
        if (page.isPaged()) {
            typedQuery.setFirstResult(page.getPageNumber() * page.getPageSize());
            typedQuery.setMaxResults(page.getPageSize());
        }

        final List<T> result = typedQuery.getResultList();
        return new PageImpl<>(result, page, count);
    }
}
