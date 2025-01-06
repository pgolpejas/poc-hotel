package com.reservation.infrastructure.repository.mongo.repository.custom;

import com.reservation.domain.dto.AggregatedReservationCriteria;
import com.reservation.infrastructure.repository.mongo.entity.AggregatedReservationDocument;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

@Repository
public interface AggregatedReservationMongoCustomRepository {

  Page<AggregatedReservationDocument> search(AggregatedReservationCriteria criteria);
}
