package com.reservation.infrastructure.repository.jpa.custom;

import com.reservation.domain.dto.AggregatedReservationCriteria;
import com.reservation.infrastructure.repository.entity.AggregatedReservationEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

@Repository
public interface AggregatedReservationJpaCustomRepository {

  Page<AggregatedReservationEntity> search(AggregatedReservationCriteria criteria);
}
