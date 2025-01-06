package com.reservation.infrastructure.repository.mongo.repository.custom;

import java.util.List;

import com.reservation.domain.dto.AggregatedReservationCriteria;
import com.reservation.infrastructure.repository.mongo.entity.AggregatedReservationDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class AggregatedReservationMongoCustomRepositoryImpl implements AggregatedReservationMongoCustomRepository {

  private final MongoTemplate mongoTemplate;

  @Override
  public Page<AggregatedReservationDocument> search(final AggregatedReservationCriteria criteria) {
    if (criteria == null) {
      return new PageImpl<>(List.of());
    }

    final Pageable pageable = criteria.limit() <= 0 ? Pageable.unpaged() : PageRequest.of(criteria.page(), criteria.limit());

    final Query query = new Query();
    query.addCriteria(Criteria.where("end").gte(criteria.from()));
    query.addCriteria(Criteria.where("start").lte(criteria.to()));

    if (!CollectionUtils.isEmpty(criteria.hotelIds())) {
      query.addCriteria(Criteria.where("hotel.id").in(criteria.hotelIds()));
    }
    if (!CollectionUtils.isEmpty(criteria.guestIds())) {
      query.addCriteria(Criteria.where("guestId").in(criteria.guestIds()));
    }
    if (!CollectionUtils.isEmpty(criteria.roomTypeIds())) {
      query.addCriteria(Criteria.where("roomTypeId").in(criteria.roomTypeIds()));

    }
    if (!CollectionUtils.isEmpty(criteria.hotelCountries())) {
      query.addCriteria(Criteria.where("hotel.country").in(criteria.hotelCountries()));
    }

    query.with(
        Sort.by(criteria.sortDirection().equalsIgnoreCase("ASC") ? Sort.Order.asc(criteria.sortBy()) : Sort.Order.desc(criteria.sortBy())));

    if (pageable.isPaged()) {
      query.skip(pageable.getOffset());
      query.limit(pageable.getPageSize());
    }

    final List<AggregatedReservationDocument> resultList = this.mongoTemplate.find(query, AggregatedReservationDocument.class);

    long count = 0;
    if (pageable.isPaged()) {
      count = this.mongoTemplate.count(query, AggregatedReservationDocument.class);
    }

    return new PageImpl<>(resultList, pageable, pageable.isUnpaged() ? resultList.size() : count);
  }

}
