package com.reservation.infrastructure.repository.jpa.custom;

import static com.hotel.core.infrastructure.database.helper.JsonQueryHelper.buildFilterRelated;
import static com.reservation.infrastructure.repository.mapper.AggregatedReservationCriteriaMapper.mapToJsonQueryDto;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.reservation.domain.dto.AggregatedReservationCriteria;
import com.reservation.infrastructure.repository.entity.AggregatedReservationEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class AggregatedReservationJpaCustomRepositoryImpl implements AggregatedReservationJpaCustomRepository {

  private static final String SEARCH_SQL = """
              select ar.id,
                    ar.hotel_id,
                    ar.start_date,
                    ar.end_date,
                    ar.aggregate,
                    ar.created_at,
                    ar.modified_at,
                    ar.modified_by
              from aggregated_reservation ar
              where 1=1
      """;

  private static final String COUNT_SEARCH_SQL = """
        select count(*)
        from aggregated_reservation ar
        where 1=1
      """;

  private final EntityManager entityManager;

  @Override
  @SuppressWarnings({"unchecked", "SqlSourceToSinkFlow", "java:S2589"})
  public Page<AggregatedReservationEntity> search(final AggregatedReservationCriteria criteria) {
    if (criteria == null) {
      return new PageImpl<>(List.of());
    }

    final Pageable pageable = criteria.limit() <= 0 ? Pageable.unpaged() : PageRequest.of(criteria.page(), criteria.limit());

    final Map<String, Object> parameters = new HashMap<>();
    final StringBuilder queryFilters = new StringBuilder();
    final StringBuilder searchSql = new StringBuilder(SEARCH_SQL);

    if (Objects.nonNull(criteria.from())) {
      parameters.put("fromDate", criteria.from());
      queryFilters.append(" AND ar.end_date >= :fromDate ");
    }
    if (Objects.nonNull(criteria.to())) {
      parameters.put("toDate", criteria.to());
      queryFilters.append(" AND ar.start_date <= :toDate ");
    }
    if (!CollectionUtils.isEmpty(criteria.hotelIds())) {
      parameters.put("hotelIds", criteria.hotelIds());
      queryFilters.append(" AND ar.hotel_id IN(:hotelIds) ");
    }

    final Set<Set<String>> jsonFilters = buildFilterRelated(new LinkedHashSet<>(), mapToJsonQueryDto(criteria));

    if (!CollectionUtils.isEmpty(jsonFilters)) {
      queryFilters.append(" AND (")
          .append(jsonFilters.stream()
              .map(filtersList -> "aggregate @> '{" + String.join(",\n ", filtersList) + "\n}'")
              .collect(Collectors.joining(" \nOR ")))
          .append(")");
    }

    searchSql.append(queryFilters)
        .append(" order by ")
        .append(criteria.sortBy())
        .append(" ")
        .append(criteria.sortDirection());

    final Query query = this.entityManager.createNativeQuery(searchSql.toString(), AggregatedReservationEntity.class);
    parameters.keySet().forEach(parameterName -> query.setParameter(parameterName, parameters.get(parameterName)));
    if (pageable.isPaged()) {
      query.setFirstResult(criteria.page() * criteria.limit());
      query.setMaxResults(criteria.limit());
    }
    final List<AggregatedReservationEntity> resultList = query.getResultList();

    // Count query
    long count = 0L;
    if (pageable.isPaged()) {
      final Query countQuery = this.entityManager.createNativeQuery(COUNT_SEARCH_SQL + queryFilters, Long.class);
      parameters.keySet().forEach(parameterName -> countQuery.setParameter(parameterName, parameters.get(parameterName)));
      count = (Long) countQuery.getSingleResult();
    }

    return new PageImpl<>(resultList, pageable, pageable.isUnpaged() ? resultList.size() : count);
  }

}
